package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.sibi.helpi.models.Resource;
import com.sibi.helpi.repositories.PostRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class OfferPostViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private OfferPostViewModel viewModel;
    private MockedStatic<PostRepository> mockedPostRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Intercept the static call to PostRepository.getInstance()
        mockedPostRepository = Mockito.mockStatic(PostRepository.class);
        // Return a dummy PostRepository to avoid firebase initialization
        PostRepository dummyPostRepository = Mockito.mock(PostRepository.class);
        mockedPostRepository.when(PostRepository::getInstance).thenReturn(dummyPostRepository);

        viewModel = new OfferPostViewModel();
    }

    @After
    public void tearDown() {
        mockedPostRepository.close();
    }

    @Test
    public void testGetPostLiveData() {
        // Set a value on the LiveData and verify that getPostLiveData returns it.
        Resource<String> resource = Resource.success("Post Successful");
        viewModel.getPostLiveData().setValue(resource);
        LiveData<Resource<String>> liveData = viewModel.getPostLiveData();
        assertEquals(resource, liveData.getValue());
    }
}
