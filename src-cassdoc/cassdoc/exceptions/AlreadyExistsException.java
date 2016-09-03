package cassdoc.exceptions;


/**
 * For when CQL detects something already exists and that wasn't expected (create, etc).
 * 
 * @author a999166
 */

public class AlreadyExistsException extends RuntimeException
{

  public AlreadyExistsException()
  {
  }

  public AlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public AlreadyExistsException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public AlreadyExistsException(String message)
  {
    super(message);
  }

  public AlreadyExistsException(Throwable cause)
  {
    super(cause);
  }

  private static final long serialVersionUID = 3289794338789733L;

}
