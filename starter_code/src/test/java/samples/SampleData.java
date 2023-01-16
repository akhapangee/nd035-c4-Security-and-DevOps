package samples;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import org.assertj.core.util.Lists;

import java.math.BigDecimal;

public class SampleData {
    public static User getSampleUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("password1234");

        Cart cart = new Cart();
        BigDecimal total = new BigDecimal("00.00");
        cart.setId(1L);
        cart.setItems(Lists.newArrayList(getSampleItem()));
        cart.setUser(user);
        cart.setTotal(total.add(getSampleItem().getPrice()));

        user.setCart(cart);

        return user;
    }

    public static Item getSampleItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(new BigDecimal("10.10"));
        item.setDescription("Test Item Desc");
        return item;
    }
}
