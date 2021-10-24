package rechard.learn.reactivecode.demo;

import java.util.function.*;

/**
 * 方法引用
 * @author Rechard
 **/
public class MethodReferenceDemo {

    static class Person{

        private String name="default name";

        public Person(){}

        public Person(String name) {
            this.name = name;
        }

        public static String gretting(Person p){
            return p.name+": hello!";
        }

        public String info(String prefix){
            return prefix+" : "+this.name;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        //静态方法引用
       Consumer p=System.out::println;
       p.accept("hello world");

       //类里的静态方法的引用
        Person person = new Person("rechard");
        Function<Person, String> gretting = Person::gretting;
        System.out.println(gretting.apply(person));

        //使用类名来引用
        BiFunction<Person,String,String> personInfo =Person::info;
        String info=personInfo.apply(person,"personInfo");
        System.out.println(info);

        //构造方法的引用
        //无参的构造方法
        Supplier<Person> supplier=Person::new;
        System.out.println("创建了对象:"+supplier.get());

        //有参的构造方法
        Function<String ,Person> function=Person::new;
        System.out.println("创建了对象:"+function.apply("rechard"));
    }
}
