//Raddon On Top!

package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.COM.*;

public class TlbFunctionVTable extends TlbAbstractMethod
{
    public TlbFunctionVTable(final int count, final int index, final TypeLibUtil typeLibUtil, final OaIdl.FUNCDESC funcDesc, final TypeInfoUtil typeInfoUtil) {
        super(index, typeLibUtil, funcDesc, typeInfoUtil);
        final String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
        if (this.paramCount > 0) {
            this.methodvariables = ", ";
        }
        for (int i = 0; i < this.paramCount; ++i) {
            final OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            final String methodName = names[i + 1].toLowerCase();
            this.methodparams = this.methodparams + this.getType(elemdesc.tdesc) + " " + this.replaceJavaKeyword(methodName);
            this.methodvariables += methodName;
            if (i < this.paramCount - 1) {
                this.methodparams += ", ";
                this.methodvariables += ", ";
            }
        }
        this.replaceVariable("helpstring", this.docStr);
        this.replaceVariable("returntype", this.returnType);
        this.replaceVariable("methodname", this.methodName);
        this.replaceVariable("methodparams", this.methodparams);
        this.replaceVariable("methodvariables", this.methodvariables);
        this.replaceVariable("vtableid", String.valueOf(this.vtableId));
        this.replaceVariable("memberid", String.valueOf(this.memberid));
        this.replaceVariable("functionCount", String.valueOf(count));
    }
    
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionVTable.template";
    }
}
