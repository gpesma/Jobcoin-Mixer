import java.math.BigDecimal;


public class Wallet {
	BigDecimal total, current;
	String address;
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getCurrent() {
		return current;
	}
	public void setCurrent(BigDecimal current) {
		this.current = current;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void incrementByDime() {
		this.current = this.current.add(new BigDecimal("0.1"));
	}
	
	public void incrementBy(String toAdd) {
		this.current = this.current.add(new BigDecimal(toAdd));
	}
	
	public Wallet(String totalAmount, String address) {
		this.total = new BigDecimal(totalAmount);
		this.address = address;
		this.current = new BigDecimal("0");
	}
	

}
