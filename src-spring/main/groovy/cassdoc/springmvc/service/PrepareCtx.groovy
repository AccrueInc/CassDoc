package cassdoc.springmvc.service

import cassdoc.Detail
import cassdoc.OperationContext
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

@CompileStatic
@Component
class PrepareCtx {
    CtxDtl docExists(String collection, Detail customDetail) { ctxAndDtl(collection, customDetail) }

    CtxDtl ctxAndDtl(String collection, Detail customDetail) {
        OperationContext ctx = new OperationContext(space: collection)
        new CtxDtl(ctx: ctx, dtl: customDetail == null ? new Detail() : customDetail)
    }
}

@CompileStatic
class CtxDtl {
    OperationContext ctx
    Detail dtl
}