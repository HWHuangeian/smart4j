package org.smart4j.framework;

import org.smart4j.framework.helper.AopHelper;
import org.smart4j.framework.helper.BeanHelper;
import org.smart4j.framework.helper.ClassHelper;
import org.smart4j.framework.helper.ControllerHelper;
import org.smart4j.framework.helper.IocHelper;
import org.smart4j.framework.util.ClassUtil;

/**
 * 加载相应的 Helper 类
 *
 * @author huangyong
 * @since 1.0.0
 */
public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
            // 加载配置信息，获取配置信息中的指定包名，然后获取指定包名下的所有类的Class类型
            ClassHelper.class,
            // 为标注了@Controller或@Service的Class类型创建单例对象，放入IOC容器
            BeanHelper.class,
            // 为标注了@AspectJ的Class类型创建代理对象集，放入IOC容器
            AopHelper.class,
            // 检查容器中的每个类，为标注了@Inject的域注入依赖
            IocHelper.class,
            // 解析Controller的Action注解，生成保存Request和Handler映射关系的map
            ControllerHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}