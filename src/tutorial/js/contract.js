// generated with yass null

'use strict';

var contract = {};

contract.ClientServices = {
  PriceListener: 0 /* contract.PriceListener */
};

contract.ServerServices = {
  PriceEngine: 0 /* contract.PriceEngine */,
  InstrumentService: 1 /* contract.InstrumentService */
};

contract.InstrumentService = function () {};
contract.InstrumentService.prototype.getInstruments = function () {};
contract.InstrumentService.prototype.reload = function () {};

contract.PriceEngine = function () {};
contract.PriceEngine.prototype.subscribe = function (param0) {};

contract.PriceListener = function () {};
contract.PriceListener.prototype.newPrices = function (param0) {};

contract.PriceType = yass.enumConstructor();
contract.PriceType.BID = new contract.PriceType(0, "BID");
contract.PriceType.ASK = new contract.PriceType(1, "ASK");
yass.enumDesc(6, contract.PriceType);

contract.Price = function () {
  this.instrumentId = null;
  this.type = null;
  this.value = null;
};
yass.inherits(contract.Price, yass.Class);
yass.classDesc(7, contract.Price);

contract.instrument = {};

contract.Instrument = function () {
  this.id = null;
  this.name = null;
};
yass.inherits(contract.Instrument, yass.Class);

contract.instrument.Stock = function () {
  contract.Instrument.call(this);
  this.paysDividend = null;
};
yass.inherits(contract.instrument.Stock, contract.Instrument);
yass.classDesc(8, contract.instrument.Stock);

contract.instrument.Bond = function () {
  contract.Instrument.call(this);
  this.coupon = null;
};
yass.inherits(contract.instrument.Bond, contract.Instrument);
yass.classDesc(9, contract.instrument.Bond);

contract.UnknownInstrumentsException = function () {
  this.comment = null;
  this.instrumentIds = null;
};
yass.inherits(contract.UnknownInstrumentsException, yass.Class);
yass.classDesc(10, contract.UnknownInstrumentsException);

yass.classField(contract.Price, 1, "instrumentId", yass.STRING);
yass.classField(contract.Price, 2, "type", contract.PriceType);
yass.classField(contract.Price, 3, "value", yass.INTEGER);

yass.classField(contract.instrument.Stock, 1, "id", yass.STRING);
yass.classField(contract.instrument.Stock, 2, "name", yass.STRING);
yass.classField(contract.instrument.Stock, 3, "paysDividend", yass.BOOLEAN);

yass.classField(contract.instrument.Bond, 1, "coupon", yass.INTEGER);
yass.classField(contract.instrument.Bond, 2, "id", yass.STRING);
yass.classField(contract.instrument.Bond, 3, "name", yass.STRING);

yass.classField(contract.UnknownInstrumentsException, 1, "comment", null);
yass.classField(contract.UnknownInstrumentsException, 2, "instrumentIds", yass.LIST);

contract.SERIALIZER = new yass.Serializer(contract);
