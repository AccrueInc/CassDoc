package cassdoc.exceptions;


/**
 * General error in persistence occurred
 * 
 * @author a999166
 */

public class PersistenceException extends RuntimeException
{

  public PersistenceException()
  {
  }

  public PersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public PersistenceException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public PersistenceException(String message)
  {
    super(message);
  }

  public PersistenceException(Throwable cause)
  {
    super(cause);
  }

  private static final long serialVersionUID = 321976438729576L;

}
