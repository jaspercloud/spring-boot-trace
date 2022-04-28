package io.github.jaspercloud.trace;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@Intercepts({
        @Signature(type = Executor.class, method = "update",
                args = {
                        MappedStatement.class,
                        Object.class
                }),
        @Signature(type = Executor.class, method = "query",
                args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                        ResultHandler.class,
                        CacheKey.class,
                        BoundSql.class
                }),
        @Signature(type = Executor.class, method = "query",
                args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                        ResultHandler.class
                }),
        @Signature(type = Executor.class, method = "queryCursor",
                args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class
                })
})
public class MybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        TraceLog traceLog = TraceContext.get();
        Object ret = invocation.proceed();
        return ret;
    }
}
