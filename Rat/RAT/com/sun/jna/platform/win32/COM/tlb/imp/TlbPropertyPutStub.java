//Raddon On Top!

package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.COM.*;

public class TlbPropertyPutStub extends TlbAbstractMethod
{
    public TlbPropertyPutStub(final int index, final TypeLibUtil typeLibUtil, final OaIdl.FUNCDESC funcDesc, final TypeInfoUtil typeInfoUtil) {
        super(index, typeLibUtil, funcDesc, typeInfoUtil);
        final TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
        final String docStr = typeInfoDoc.getDocString();
        final String methodname = "set" + typeInfoDoc.getName();
        final String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
        for (int i = 0; i < this.paramCount; ++i) {
            final OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            final String varType = this.getType(elemdesc);
            this.methodparams = this.methodparams + varType + " " + this.replaceJavaKeyword(names[i].toLowerCase());
            if (i < this.paramCount - 1) {
                this.methodparams += ", ";
            }
        }
        this.replaceVariable("helpstring", docStr);
        this.replaceVariable("methodname", methodname);
        this.replaceVariable("methodparams", this.methodparams);
        this.replaceVariable("vtableid", String.valueOf(this.vtableId));
        this.replaceVariable("memberid", String.valueOf(this.memberid));
    }
    
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPutStub.template";
    }
}
