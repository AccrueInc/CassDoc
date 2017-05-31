package cassdoc

import groovy.transform.CompileStatic

@CompileStatic
class DocField {
    String docUUID
    String name
    FieldValue value
    List<String> childDocs
    String metaID
}


@CompileStatic
class FieldValue {
    Class type;
    String value;
}

// near / far space info ?
// cascade delete?

@CompileStatic
class Rel {
    String p1 = ""
    String p2 = ""
    String p3 = ""
    String p4 = ""
    String ty1 = ""
    String ty2 = ""
    String ty3 = ""
    String ty4 = ""
    String c1 = ""
    String c2 = ""
    String c3 = ""
    String c4 = ""
    String d
    String lk
    String sp
    String cscd
    String z_md


    RelKey getRelKey() {
        RelKey rk = new RelKey()
        rk.p1 = p1
        rk.ty1 = ty1
        rk.ty2 = ty2
        rk.ty3 = ty3
        rk.ty4 = ty4
        rk.p2 = p2
        rk.p3 = p3
        rk.p4 = p4
        rk.c1 = c1
        rk.c2 = c2
        rk.c3 = c3
        rk.c4 = c4
        return rk
    }
}

@CompileStatic
class RelKey {
    String p1 = ""
    String p2 = ""
    String p3 = ""
    String p4 = ""
    String ty1 = ""
    String ty2 = ""
    String ty3 = ""
    String ty4 = ""
    String c1 = ""
    String c2 = ""
    String c3 = ""
    String c4 = ""

    boolean isp2 = false
    boolean isp3 = false
    boolean isp4 = false
    boolean isty1 = false
    boolean isty2 = false
    boolean isty3 = false
    boolean isty4 = false
    boolean isc1 = false
    boolean isc2 = false
    boolean isc3 = false
    boolean isc4 = false

    public void setP2(String v) { isp2 = true; p2 = v }

    public void setP3(String v) { isp3 = true; p3 = v }

    public void setP4(String v) { isp4 = true; p4 = v }

    public void setTy1(String v) { isty1 = true; ty1 = v }

    public void setTy2(String v) { isty2 = true; ty2 = v }

    public void setTy3(String v) { isty3 = true; ty3 = v }

    public void setTy4(String v) { isty4 = true; ty4 = v }

    public void setC1(String v) { isc1 = true; c1 = v }

    public void setC2(String v) { isc2 = true; c2 = v }

    public void setC3(String v) { isc3 = true; c3 = v }

    public void setC4(String v) { isc4 = true; c4 = v }

}
