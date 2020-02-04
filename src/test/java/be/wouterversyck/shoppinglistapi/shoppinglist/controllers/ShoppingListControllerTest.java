package be.wouterversyck.shoppinglistapi.shoppinglist.controllers;

import be.wouterversyck.shoppinglistapi.shoppinglist.models.ShoppingListDto;
import be.wouterversyck.shoppinglistapi.shoppinglist.services.ShoppingListService;
import be.wouterversyck.shoppinglistapi.shoppinglist.services.ShoppingListServiceTest;
import be.wouterversyck.shoppinglistapi.users.models.User;
import be.wouterversyck.shoppinglistapi.users.services.UserService;
import com.sun.net.httpserver.HttpPrincipal;
import lombok.Builder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingListControllerTest {

    @Mock
    private ShoppingListService shoppingListService;

    @Mock
    private UserService userService;

    @Mock
    private MockHttpServletRequest httpServletRequest;

    @InjectMocks
    private ShoppingListController shoppingListController;

    private static final String USERNAME = "USERNAME";

    @Test
    public void shouldReturnLists_WhenUserIsSetInContext() {
        User user = new User();
        user.setUsername(USERNAME);
        when(httpServletRequest.getUserPrincipal()).thenReturn(new HttpPrincipal(USERNAME, "REALM"));
        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
        when(shoppingListService.getShoppingListsForUser(user)).thenReturn(getShoppingLists());

        List<ShoppingListDto> items = shoppingListController.getShoppingLists(httpServletRequest);

        assertThat(items).hasSize(2);
        assertThat(items).extracting("name")
                .contains("test", "test2");
        assertThat(items).extracting("id")
                .contains(1L, 2L);
    }

    private List<ShoppingListDto> getShoppingLists() {
        return Arrays.asList(
                ShoppingListServiceTest.ShoppingList.builder()
                        .id(1)
                        .name("test")
                        .build(),
                ShoppingListServiceTest.ShoppingList.builder()
                        .id(2)
                        .name("test2")
                        .build()
        );
    }
}
