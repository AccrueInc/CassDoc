package cassdoc.jcr

import javax.jcr.AccessDeniedException
import javax.jcr.Binary
import javax.jcr.InvalidItemStateException
import javax.jcr.Item
import javax.jcr.ItemExistsException
import javax.jcr.ItemNotFoundException
import javax.jcr.ItemVisitor
import javax.jcr.Node
import javax.jcr.Property
import javax.jcr.ReferentialIntegrityException
import javax.jcr.RepositoryException
import javax.jcr.Session
import javax.jcr.Value
import javax.jcr.ValueFormatException
import javax.jcr.lock.LockException
import javax.jcr.nodetype.ConstraintViolationException
import javax.jcr.nodetype.NoSuchNodeTypeException
import javax.jcr.nodetype.PropertyDefinition
import javax.jcr.version.VersionException

class CassDocJcrProperty implements Property {

  @Override
  public Binary getBinary() throws ValueFormatException, RepositoryException {
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PropertyDefinition getDefinition() throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double getDouble() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getLength() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long[] getLengths() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getLong() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Node getNode() throws ItemNotFoundException, ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Property getProperty() throws ItemNotFoundException, ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getStream() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getString() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getType() throws RepositoryException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Value getValue() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Value[] getValues() throws ValueFormatException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isMultiple() throws RepositoryException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setValue(BigDecimal arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(Binary arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(boolean arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(Calendar arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(double arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(InputStream arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(long arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(Node arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(String arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(String[] arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(Value arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValue(Value[] arg0) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void accept(ItemVisitor arg0) throws RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public Item getAncestor(int arg0) throws ItemNotFoundException, AccessDeniedException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getDepth() throws RepositoryException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getName() throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getPath() throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Session getSession() throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isModified() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isNew() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isNode() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isSame(Item arg0) throws RepositoryException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void refresh(boolean arg0) throws InvalidItemStateException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void remove() throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
    // TODO Auto-generated method stub

  }

  @Override
  public Object getProperty(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }
}
