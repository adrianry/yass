package ch.softappeal.yass.core.remote;

import ch.softappeal.yass.util.Check;
import ch.softappeal.yass.util.Nullable;

/**
 * A remote request.
 */
public final class Request extends Message {

  private static final long serialVersionUID = 1L;

  public final Object serviceId;

  /**
   * @see MethodMapper.Mapping#id
   */
  public final Object methodId;

  @Nullable public final Object[] arguments;

  public Request(@Nullable final Object context, final Object serviceId, final Object methodId, @Nullable final Object[] arguments) {
    super(context);
    this.serviceId = Check.notNull(serviceId);
    this.methodId = Check.notNull(methodId);
    //noinspection AssignmentToCollectionOrArrayFieldFromParameter
    this.arguments = arguments;
  }

}
