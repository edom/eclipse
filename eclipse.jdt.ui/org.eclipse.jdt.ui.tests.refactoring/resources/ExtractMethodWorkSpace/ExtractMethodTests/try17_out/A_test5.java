package try17_out;

class Foo5 extends Exception {}
class Bar5 extends Exception {}

public class A_test5 {

	void foo() throws Exception {
		extracted();
	}

	protected void extracted() throws Bar5 {
		/*[*/try (Test5 t = new Test5()) {

		}/*]*/
	}
}

class Test5 implements AutoCloseable {
	@Override
	public void close() throws Bar5 {
	}
}
