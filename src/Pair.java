import java.io.Serializable;

public class Pair implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String string;
		private Integer integer;
		
		public Pair(String string, Integer integer) {
			this.setString(string);
			this.setInteger(integer);
		}

		public Integer getInteger() {
			return integer;
		}

		public void setInteger(Integer integer) {
			this.integer = integer;
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}
	}