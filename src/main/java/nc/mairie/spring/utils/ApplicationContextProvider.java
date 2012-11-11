package nc.mairie.spring.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;
	
	public ApplicationContextProvider() {
		
	}
	
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		ApplicationContextProvider.setContext(arg0);
	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static void setContext(ApplicationContext context) {
		ApplicationContextProvider.context = context;
	}

}
