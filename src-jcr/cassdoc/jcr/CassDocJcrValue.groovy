package cassdoc.jcr

import javax.jcr.Binary
import javax.jcr.Property
import javax.jcr.RepositoryException
import javax.jcr.Value
import javax.jcr.ValueFormatException

class CassDocJcrValue implements Value {
  String jsonType

  transient Property prop
  transient CassDocJcrRepository


  @Override
  public Binary getBinary() throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean getBoolean() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Calendar getDate() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BigDecimal getDecimal() throws ValueFormatException, RepositoryException {
    if (jsonType == 'D')
      // TODO Auto-generated method stub
      return null;
  }

  @Override
  public double getDouble() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getLong() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public InputStream getStream() throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getString() throws ValueFormatException, IllegalStateException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getType() {
    // TODO Auto-generated method stub
    return 0;
  }
}
