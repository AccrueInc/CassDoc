package cassdoc.exceptions;


/**
 * General error in persistence occurred
 * 
 * @author a999166
 */

public class PersistenceTooLargeException extends RuntimeException
{

  public PersistenceTooLargeException()
  {
  }

  public PersistenceTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public PersistenceTooLargeException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public PersistenceTooLargeException(String message)
  {
    super(message);
  }

  public PersistenceTooLargeException(Throwable cause)
  {
    super(cause);
  }

  private static final long serialVersionUID = 321976438729576L;

}
