package cassdoc.exceptions;


/**
 * For when an expected value is not found in the database (dangling ids/relations/links/etc)
 * 
 * @author a999166
 */

public class NotFoundException extends RuntimeException
{

  public NotFoundException()
  {
  }

  public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public NotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public NotFoundException(String message)
  {
    super(message);
  }

  public NotFoundException(Throwable cause)
  {
    super(cause);
  }

  private static final long serialVersionUID = 638979438789573L;

}
