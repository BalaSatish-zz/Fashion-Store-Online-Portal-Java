
public class ShopItems {
	
	private String name;
	private String type;
	private int cost;
	private int quantity;
	
	public ShopItems() {
	}
	
	public ShopItems(String type,String name,int cost){
		this.name = name;
		this.cost = cost;
		this.type = type;
	}
	
	public String getName(){
		return this.name;
	}
	public String getType(){
		return this.type;
	}
	public int getCost(){
		return this.cost;
	}
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}
	public int getQuantity(){
		return this.quantity;
	}
}
