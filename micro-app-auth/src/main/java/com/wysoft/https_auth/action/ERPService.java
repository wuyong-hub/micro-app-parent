package com.wysoft.https_auth.action;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysoft.https_auth.dao.AddressDao;
import com.wysoft.https_auth.dao.OrderDao;
import com.wysoft.https_auth.dao.OrderItemDao;
import com.wysoft.https_auth.dao.ProductDao;
import com.wysoft.https_auth.dao.UserDao;
import com.wysoft.https_auth.model.CustomerAddr;
import com.wysoft.https_auth.model.Order;
import com.wysoft.https_auth.model.OrderItem;
import com.wysoft.https_auth.model.Product;
import com.wysoft.https_auth.model.UaamUser;
import com.wysoft.https_base.action.BaseService;
import com.wysoft.https_base.annotation.RemoteMethod;
import com.wysoft.https_base.util.JSONUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("erpService")
public class ERPService extends BaseService{
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private OrderItemDao orderItemDao;
	
	@Autowired
	private AddressDao addrDao;
	
	@Autowired
	private ProductDao productDao;
	
	@RemoteMethod
	public JSONObject placeOrder(JSONObject json) {
		JSONObject data = json.getJSONObject("data");
		if(data == null) {
			return JSONUtil.getErrMsg("下单数据为空！");
		}
		
		int userId = data.getInt("userId");
		double total = data.getDouble("total");
		String addrId = data.getString("addrId");
		JSONArray items = data.getJSONArray("items");
		
		UaamUser user = userDao.findById(userId).get();
		
		CustomerAddr addr = addrDao.findById(addrId).get();
		
		Order order = new Order();
		order.setUser(user);
		order.setAddr(addr);
		order.setTotal(total);
		order.setOrderTime(new Timestamp(System.currentTimeMillis()));
		order.setStatus(0);
		
		order = orderDao.save(order);
		
		for(int i = 0; i < items.size(); i ++) {
			JSONObject item = items.getJSONObject(i);
			String productId = item.getString("productId");
			int itemCount = item.getInt("itemCount");
			
			Product product = productDao.findById(productId).get();
			
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setItemCount(itemCount);
			orderItemDao.save(orderItem);
		}
		
		return JSONUtil.getResult("下单成功！");
	}
}
