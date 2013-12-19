package ch.softappeal.yass.tutorial.session.contract;

import ch.softappeal.yass.core.remote.MethodMapper;
import ch.softappeal.yass.core.remote.MethodMappers;
import ch.softappeal.yass.serialize.FastReflector;
import ch.softappeal.yass.serialize.Serializer;
import ch.softappeal.yass.serialize.TypeConverters;
import ch.softappeal.yass.serialize.fast.AbstractFastSerializer;
import ch.softappeal.yass.serialize.fast.TaggedFastSerializer;
import ch.softappeal.yass.serialize.fast.TypeConverterId;
import ch.softappeal.yass.transport.MessageSerializer;
import ch.softappeal.yass.transport.PacketSerializer;
import ch.softappeal.yass.tutorial.session.contract.instrument.Bond;
import ch.softappeal.yass.tutorial.session.contract.instrument.Stock;
import ch.softappeal.yass.util.Dumper;

import java.math.BigDecimal;
import java.util.Arrays;

public final class Config {

  public static final AbstractFastSerializer CONTRACT_SERIALIZER = new TaggedFastSerializer(
    FastReflector.FACTORY,
    Arrays.<TypeConverterId>asList(
      new TypeConverterId(TypeConverters.BIGDECIMAL_TO_STRING, 0),
      new TypeConverterId(DateTime.TO_STRING, 50)
    ),
    Arrays.<Class<?>>asList(PriceType.class), // enumerations
    Arrays.<Class<?>>asList(Price.class, Trade.class, UnknownInstrumentsException.class), // concrete classes
    Arrays.<Class<?>>asList(Stock.class, Bond.class) // referenceable concrete classes
  );

  public static final Serializer PACKET_SERIALIZER = new PacketSerializer(new MessageSerializer(CONTRACT_SERIALIZER));

  public static final Dumper DUMPER = new Dumper(BigDecimal.class, DateTime.class);

  public static final MethodMapper.Factory METHOD_MAPPER_FACTORY = MethodMappers.TAG_FACTORY;

}
