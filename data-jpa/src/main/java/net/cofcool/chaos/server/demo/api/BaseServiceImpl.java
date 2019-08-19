/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.demo.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.QueryResult;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.util.BeanUtils;
import net.cofcool.chaos.server.data.jpa.support.SimpleJpaService;
import net.cofcool.chaos.server.data.jpa.util.SpecificationUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service 基本实现
 */
public abstract class BaseServiceImpl<T, ID, J extends JpaRepository<T, ID>> extends
    SimpleJpaService<T, ID, J> implements BaseService<T> {

    @Override
    protected Object queryWithPage(Page<T> condition, T entity) {
        Specification<T> sp = getSp();

        if (condition.getStartDate() >= 0) {
            sp = SpecificationUtils.andGreaterOrEqualTo(sp, "startTime", Page.convertToDate(condition.getStartDate()));
        }
        if (condition.getEndDate() >= 0) {
            sp = SpecificationUtils.andLessOrEqualTo(sp, "endTime", Page.convertToDate(condition.getEndDate()));
        }

        if (shouldCheck()) {
            sp = checkId(sp, entity);
        }

        return queryWithSp(sp, condition, entity);
    }

    @Override
    public QueryResult<T, ?> simpleQuery(Page<T> condition, T entity) {
        Page<T> page = Page.checkPage(condition);

        Objects.requireNonNull(getPageProcessor());
        return QueryResult.of(
            getPageProcessor().process(page, super.queryWithPage(page, entity)),
            getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
            getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC)
        );
    }

    @Override
    public ExecuteResult<List<T>> simpleQueryAll(T entity) {
        return super.queryAll(entity);
    }

    @Override
    public ExecuteResult<T> simpleQueryById(T entity) {
        return super.queryById(entity);
    }

    /**
     * 是否需要检查 {@link #ROLE_IDS}
     * @return 是否需要检查
     */
    protected boolean shouldCheck() {
        return true;
    }

    @Override
    public ExecuteResult<T> queryById(T entity) {
        if (getEntityId(entity) != null) {
            return super.queryById(entity);
        } else {
            return createExecuteResultBy(getJpaRepository().findOne(Example.of(entity)));
        }
    }


    @Override
    public ResultState delete(T entity) {
        if (physicDelete()) {
            return super.delete(entity);
        }

        ExecuteResult<T> result = queryById(entity);

        if (result.successful()) {
            logicDeleteCallback().apply(result.entity());

            return update(result.entity()).state();
        }

        return result.state();
    }

    /**
     * 是否物理删除
     * @return 是否物理删除
     */
    protected boolean physicDelete() {
        return true;
    }

    /**
     * 处理逻辑删除前的逻辑, 即修改数据删除标志等
     * @return Function
     */
    protected Function<T, T> logicDeleteCallback() {
        return null;
    }

    protected Specification<T> getSp() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    protected abstract Object queryWithSp(Specification<T> sp, Page<T> condition, T entity);

    protected Specification<T> checkId(Specification<T> sp, T entity) {

        for (String id : ROLE_IDS) {
            try {
                Long v = (Long) BeanUtils.getPropertyDescriptor(entity.getClass(), id).getReadMethod().invoke(entity);
                sp = andEqual(sp, v, id);
            } catch (IllegalAccessException | InvocationTargetException | NullPointerException ignore) {
            }
        }

        return sp;
    }

    public static  <T, Y extends Comparable<? super Y>> Specification<T> andEqual(Specification<T> sp, Y val, String property) {
        if (val != null) {
            sp = SpecificationUtils.andEqualTo(sp, property, val);
        }

        return sp;
    }

    public static  <T, Y extends Comparable<? super Y>> Specification<T> andLike(Specification<T> sp, Y val, String property) {
        if (val != null) {
            sp = SpecificationUtils.andLikeTo(sp, property, val, true);
        }

        return sp;
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andGreater(Specification<T> sp, Y val, String property) {
        if (val != null) {
            sp = SpecificationUtils.andGreaterOrEqualTo(sp, property, val);
        }

        return sp;
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andLess(Specification<T> sp, Y val, String property) {
        if (val != null) {
            sp = SpecificationUtils.andLessOrEqualTo(sp, property, val);
        }

        return sp;
    }

    public <R> ExecuteResult<R> createExecuteResultBy(Optional<R> data) {
        return data.map(t ->
            ExecuteResult.of(t, ResultState.SUCCESSFUL, getExceptionCodeManager().getCode(
                ExceptionCodeDescriptor.SERVER_OK),
                getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC)))
            .orElseGet(() ->
                ExecuteResult.of(null, ResultState.FAILURE,
                    getExceptionCodeManager().getCode(ExceptionCodeDescriptor.OPERATION_ERR),
                    getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.OPERATION_ERR_DESC)));
    }

    public static List<String> ROLE_IDS = Arrays.asList("userId");
    public static List<String> ROLE_P_IDS = ROLE_IDS;

    public static void copyBaseId(Object target, Object data) {
        for (String id : ROLE_P_IDS) {
            try {
                Object val = BeanUtils.getPropertyDescriptor(data.getClass(), id).getReadMethod().invoke(data);
                BeanUtils.getPropertyDescriptor(target.getClass(), id).getWriteMethod().invoke(target, val);
            } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            }
        }
    }


    protected <E> ExecuteResult<E> getResult(E entity, ResultState state) {
        if (state == ResultState.SUCCESSFUL) {
            return ExecuteResult
                .of(entity, state, getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK), getExceptionCodeManager().getDescription(
                    ExceptionCodeDescriptor.SERVER_OK_DESC));
        } else {
            return ExecuteResult
                .of(entity, state, getExceptionCodeManager().getCode(ExceptionCodeDescriptor.OPERATION_ERR), getExceptionCodeManager().getDescription(
                    ExceptionCodeDescriptor.OPERATION_ERR_DESC));
        }
    }

}