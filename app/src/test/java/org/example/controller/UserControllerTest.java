package org.example.controller;

import org.aspectj.lang.annotation.Before;
import org.assertj.core.internal.bytebuddy.implementation.bind.annotation.Default;
import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.view.IHomeView;
import org.example.view.ILoginView;
import org.example.view.ISheetView;
import org.example.view.SheetView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class UserControllerTest {

  private IUserController controller;
  private ILoginView loginViewMock;

  private IHomeView homeViewMock;

  private IAppUser appUser;

  private ISheetView sheetView;

  @BeforeEach
  public void setUp() {
    loginViewMock = mock(ILoginView.class);
    homeViewMock = mock(IHomeView.class);
    this.appUser = new AppUser();
    controller = new UserController(loginViewMock, homeViewMock, appUser);
    sheetView = mock(ISheetView.class);
  }

  @Test
  public void testIsUserAuthenticationComplete() {
    assertTrue(controller.isUserAuthenticationComplete("test", "test"));
    assertFalse(controller.isUserAuthenticationComplete("", "1"));
    assertFalse(controller.isUserAuthenticationComplete("e", ""));
  }

  @Test
  public void testIsUserCreated() {
    appUser.createAccount("test1", "test1");
    assertTrue(controller.isUserCreatedSuccessfully("test1", "test1"));
    assertFalse(controller.isUserCreatedSuccessfully("", "123"));
  }

  @Test
  public void testSetCurrentSheetAndGetCurrentSheet() {
    controller.setCurrentSheet(sheetView);
    assertEquals(controller.getCurrentSheet(), sheetView);
  }

  @Test
  public void testCreateNewSheet() {
    ISheetView newSheet = mock(ISheetView.class);
    controller.createNewSheet(newSheet);
    assertEquals(controller.getCurrentSheet(), newSheet);
  }

  //TODO: Test saveSheet in future since it is a Bonus feature

  @Test
  public void testHandleToolbar() {
  }

  @Test
  public void testHandleStatsDropdown() {
  }

}
