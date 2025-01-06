package aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAspect {
	
	@Before("execution(public aop.domain.Product aop.service.ProductService.find(String) throws RuntimeException)")
	public void adviceDefore() {
		System.out.println("-- Before Advice --");
	}
	
	@After("execution(aop.domain.Product aop.service.ProductService.find(String))")
	public void adviceAfter() {
		System.out.println("-- After Advice --");
	}
	
	@AfterReturning("execution(* aop.service.ProductService.find(..))")
	public void adviceAfterReturning() {
		System.out.println("-- AfterReturning Advice --");
	}
	
	@AfterThrowing(value="execution(* *..*.ProductService.find(..))", throwing="ex")
	public void adviceAfterThrowing(Throwable ex) {
		System.out.println("-- AfterThrowing[" + ex.getMessage() + "]Advice --");
	}
	
	@Around("execution(* *..*.*.*(..)))")
	public Object adviceAround(ProceedingJoinPoint pjp) throws Throwable{
		System.out.println("-- Aound Advice : Before --");
		
		Object result = pjp.proceed(); // pjp에는 실행할 메서드가 저장되어 proceed를 하게 되면 실행되는것??
		
//		Object[] params = {"PC"};
//		Object result = pjp.proceed(params); // parameter를 처리하고 넣을 수 있다. 현재는 단순히 넣은 거지만 응용하여 바꿀 수 있을 듯.
		
		System.out.println("-- Aound Advice : After --");
			
		return result;
	}
}
