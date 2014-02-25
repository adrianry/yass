'use strict';

var yass;

function assert(value) {
  if (!value) {
    throw new Error("assert failed");
  }
}

function exception(action) {
  var thrown = false;
  try {
    action();
  } catch (e) {
    thrown = true;
    console.log("expected error caught:", e);
  }
  assert(thrown);
}

function writer2reader(writer) {
  var byteArray = writer.getUint8Array();
  var arrayBuffer = new ArrayBuffer(byteArray.length);
  new Uint8Array(arrayBuffer).set(byteArray);
  return new yass.Reader(arrayBuffer);
}

//----------------------------------------------------------------------------------------------------------------------
// Reader/Writer

(function () {

  var writer = new yass.Writer(1);
  writer.writeByte(123);
  writer.writeByte(210);
  writer.writeInt(0);
  writer.writeInt(21);
  writer.writeInt(25658);
  writer.writeInt(-13);
  writer.writeInt(-344554);
  writer.writeInt(2147483647);
  writer.writeInt(-2147483648);
  writer.writeVarInt(0);
  writer.writeVarInt(21);
  writer.writeVarInt(25658);
  writer.writeVarInt(-13);
  writer.writeVarInt(-344554);
  writer.writeVarInt(2147483647);
  writer.writeVarInt(-2147483648);
  writer.writeZigZagInt(0);
  writer.writeZigZagInt(21);
  writer.writeZigZagInt(25658);
  writer.writeZigZagInt(-13);
  writer.writeZigZagInt(-344554);
  writer.writeZigZagInt(2147483647);
  writer.writeZigZagInt(-2147483648);
  assert(writer.position === 74);

  var reader = writer2reader(writer);
  assert(!reader.isEmpty());
  assert(reader.readByte() === 123);
  assert(reader.readByte() === 210);
  assert(reader.readInt() === 0);
  assert(reader.readInt() === 21);
  assert(reader.readInt() === 25658);
  assert(reader.readInt() === -13);
  assert(reader.readInt() === -344554);
  assert(reader.readInt() === 2147483647);
  assert(reader.readInt() === -2147483648);
  assert(reader.readVarInt() === 0);
  assert(reader.readVarInt() === 21);
  assert(reader.readVarInt() === 25658);
  assert(reader.readVarInt() === -13);
  assert(reader.readVarInt() === -344554);
  assert(reader.readVarInt() === 2147483647);
  assert(reader.readVarInt() === -2147483648);
  assert(reader.readZigZagInt() === 0);
  assert(reader.readZigZagInt() === 21);
  assert(reader.readZigZagInt() === 25658);
  assert(reader.readZigZagInt() === -13);
  assert(reader.readZigZagInt() === -344554);
  assert(reader.readZigZagInt() === 2147483647);
  assert(reader.readZigZagInt() === -2147483648);
  assert(reader.isEmpty());
  exception(function () {
    reader.readByte();
  });

  writer = new yass.Writer(100);
  writer.writeByte(128);
  writer.writeByte(128);
  writer.writeByte(128);
  writer.writeByte(128);
  writer.writeByte(128);
  reader = writer2reader(writer);
  exception(function () {
    reader.readVarInt();
  });

  function utf8(bytes, value) {
    assert(yass.Writer.calcUtf8bytes(value) === bytes);
    var writer = new yass.Writer(100);
    writer.writeUtf8(value);
    assert(writer.position === bytes);
    var reader = writer2reader(writer);
    assert(reader.readUtf8(bytes) === value);
    assert(reader.isEmpty());
  }

  utf8(2, "><");
  utf8(3, ">\u0000<");
  utf8(3, ">\u0001<");
  utf8(3, ">\u0012<");
  utf8(3, ">\u007F<");
  utf8(4, ">\u0080<");
  utf8(4, ">\u0234<");
  utf8(4, ">\u07FF<");
  utf8(5, ">\u0800<");
  utf8(5, ">\u4321<");
  utf8(5, ">\uFFFF<");

}());

//----------------------------------------------------------------------------------------------------------------------
// Enum

(function () {
  var ask = contract.PriceType.ASK;
  console.log(ask);
  assert(ask instanceof contract.PriceType);
  assert(ask instanceof yass.Enum);
  assert(!(ask instanceof yass.TypeHandler));
  assert(ask.value === 1);
  assert(ask.name === "ASK");
  assert(ask === contract.PriceType.ASK);
  assert(ask !== contract.PriceType.BID);
  var values = ask.constructor.TYPE_DESC.handler.values;
  assert(values.length === 2);
  assert(values[0] === contract.PriceType.BID);
  assert(values[1] === contract.PriceType.ASK);

}());

//----------------------------------------------------------------------------------------------------------------------
// Class

(function () {
  var stock = new contract.instrument.Stock();
  stock.id = "1344";
  stock.name = "IBM";
  stock.paysDividend = true;
  console.log(stock);
  assert(stock instanceof yass.Class);
  assert(stock instanceof contract.Instrument);
  assert(stock instanceof contract.instrument.Stock);
  assert(!(stock instanceof contract.instrument.Bond));
}());

//----------------------------------------------------------------------------------------------------------------------
// Serializer

(function () {

  function copy(value) {
    var writer = new yass.Writer(100);
    contract.SERIALIZER.write(value, writer);
    var reader = writer2reader(writer);
    var result = contract.SERIALIZER.read(reader);
    assert(reader.isEmpty());
    return result;
  }

  assert(copy(null) === null);
  assert(copy(true));
  assert(!copy(false));
  assert(copy(1234567) === 1234567);
  assert(copy(-1234567) === -1234567);
  assert(copy("") === "");
  assert(copy("blabli") === "blabli");

  function compare(array1, array2) {
    if (array1.length !== array2.length) {
      return false;
    }
    for (var i = 0; i < array1.length; i++) {
      if (array1[i] !== array2[i]) {
        return false;
      }
    }
    return true;
  }

  assert(Array.isArray(copy([])));
  assert(copy([]).length === 0);
  assert(compare(copy([12]), [12]));
  assert(compare(copy([12, true, "bla"]), [12, true, "bla"]));

  var stock = new contract.instrument.Stock();
  stock.id = "1344";
  stock.name = "IBM";
  stock.paysDividend = true;
  stock = copy(stock);
  assert(stock.id === "1344");
  assert(stock.name === "IBM");
  assert(stock.paysDividend);
  stock.paysDividend = false;
  stock = copy(stock);
  assert(!stock.paysDividend);
  stock.paysDividend = null;
  stock = copy(stock);
  assert(stock.paysDividend === null);

  var bond = new contract.instrument.Bond();
  bond.coupon = 1234;
  bond = copy(bond);
  assert(bond.coupon === 1234);

  var e = new contract.UnknownInstrumentsException();
  e.instrumentIds = ["a", "b"];
  e.comment = bond;
  e = copy(e);
  assert(compare(e.instrumentIds , ["a", "b"]));
  assert(e.comment.coupon === 1234);

}());

//----------------------------------------------------------------------------------------------------------------------
