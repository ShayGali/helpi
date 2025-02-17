package com.sibi.helpi.viewmodels;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Tasks;
import com.sibi.helpi.models.User;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.UserRepository;
import com.sibi.helpi.stats.UserState;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
@RunWith(PowerMockRunner.class)
@PrepareForTest({UserViewModel.class, UserRepository.class, ImagesRepository.class, Looper.class})
@PowerMockIgnore({"android.", "androidx.", "org.xmlpull.", "com.google.android.gms."}) // Ignore Android dependencies
public class UserViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ImagesRepository mockImagesRepository;

    private UserViewModel userViewModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Mock static ImagesRepository.getInstance()
        PowerMockito.mockStatic(ImagesRepository.class);
        when(ImagesRepository.getInstance()).thenReturn(mockImagesRepository);

        // Mock UserRepository constructor
        PowerMockito.whenNew(UserRepository.class)
                .withNoArguments()
                .thenReturn(mockUserRepository);

        // Mock Looper.getMainLooper()
        PowerMockito.mockStatic(Looper.class);
        when(Looper.getMainLooper()).thenReturn(mock(Looper.class));

        userViewModel = new UserViewModel();
    }

    @After
    public void tearDown() {
        // delete test user
        mockUserRepository.deleteAccount();
    }

    @Test
    public void registerUser_success_updatesUserStateToSuccess() {
        // Arrange
        User testUser = new User("test@example.com", "John", "Doe", "", null);
        String password = "password";
        byte[] profileImg = new byte[0];

        // Mock successful Task
        when(mockUserRepository.registerUser(testUser, password, profileImg))
                .thenReturn(Tasks.forResult(null)); // DocumentReference is not used in success

        // Act
        userViewModel.registerUser(testUser, password, profileImg);

        // Assert
        MutableLiveData<UserState> userState = (MutableLiveData<UserState>) userViewModel.getUserState();
        UserState state = userState.getValue();
        assertNotNull(state);
        assertFalse(state.isLoading());
        assertEquals(testUser, state.getUser());
    }
}