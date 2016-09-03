package cassdoc.exceptions;


/**
 * error in retrieval: multiple rows when a single row expected, multiple values when a single value expected...
 * 
 * @author a999166
 */

public class RetrievalException extends RuntimeException
{

  public RetrievalException()
  {
  }

  public RetrievalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RetrievalException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public RetrievalException(String message)
  {
    super(message);
  }

  public RetrievalException(Throwable cause)
  {
    super(cause);
  }

  private static final long serialVersionUID = 328595838789573L;

}
