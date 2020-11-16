package clement.zentz.go4lunch.viewModels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import clement.zentz.go4lunch.models.rating.GlobalRating;
import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.repository.FirestoreRepository;
import clement.zentz.go4lunch.util.TestUtil;

public class FirestoreViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private Workmate WORKMATES2 = new Workmate(TestUtil.TEST_WORKMATE_2);

    //system under test
    private FirestoreViewModel firestoreViewModel;

    @Mock
    private FirestoreRepository firestoreRepository;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        firestoreViewModel = new FirestoreViewModel(firestoreRepository);
    }

    @Test
    public void receiveAllFirestoreWorkmatesTest() {
        //Arrange
        MutableLiveData<List<Workmate>> workmateListToExpect = new MutableLiveData<>();
        workmateListToExpect.postValue(TestUtil.TEST_WORKMATE_LIST);
        //Act
        Mockito.when(firestoreRepository.receiveAllFirestoreWorkmates()).thenReturn(workmateListToExpect);
        //Assert
        firestoreViewModel.receiveAllFirestoreWorkmates().observeForever(workmates -> {
            Assert.assertArrayEquals(workmateListToExpect.getValue().toArray(), workmates.toArray());
        });
    }

    @Test
    public void receiveAllWorkmates4ThisRestaurantTest() {
        //Arrange
        MutableLiveData<List<Workmate>> workmateListToExpect = new MutableLiveData<>();
        workmateListToExpect.postValue(TestUtil.TEST_WORKMATE_LIST);
        //Act
        Mockito.when(firestoreRepository.receiveAllWorkmates4ThisRestaurant()).thenReturn(workmateListToExpect);
        //Assert
        firestoreViewModel.receiveAllWorkmates4ThisRestaurant().observeForever(workmates -> {
            Assert.assertArrayEquals(workmateListToExpect.getValue().toArray(), workmates.toArray());
        });
    }

    @Test
    public void receiveCurrentUserWithWorkmateIdTest() {
        //Arrange
        MutableLiveData<Workmate> workmateToExpect = new MutableLiveData<>();
        workmateToExpect.postValue(TestUtil.TEST_WORKMATE_1);
        //Act
        Mockito.when(firestoreRepository.receiveCurrentUserWithWorkmateId()).thenReturn(workmateToExpect);
        //Assert
        firestoreViewModel.receiveCurrentUserWithWorkmateId().observeForever( workmate -> {
            Assert.assertEquals(workmateToExpect.getValue(), workmate);
        });
    }


    @Test
    public void receiveAllRatings4ThisRestaurantTest() {
        //Arrange
        MutableLiveData<List<Rating>> ratingListToExpect = new MutableLiveData<>();
        ratingListToExpect.postValue(TestUtil.TEST_RATING_LIST_4_THIS_RESTAURANT);
        //Act
        Mockito.when(firestoreRepository.receiveAllRatings4ThisRestaurant()).thenReturn(ratingListToExpect);
        //Assert
        firestoreViewModel.receiveAllRatings4ThisRestaurant().observeForever(ratings -> {
            Assert.assertArrayEquals(ratingListToExpect.getValue().toArray(), ratings.toArray());
        });
    }

    @Test
    public void receiveGlobalRating4ThisRestaurantTest() {
        //Arrange
        MutableLiveData<GlobalRating> globalRatingToExpect = new MutableLiveData<>();
        globalRatingToExpect.postValue(TestUtil.TEST_GLOBAL_RATING_1);
        //Act
        Mockito.when(firestoreRepository.receiveGlobalRating4ThisRestaurant()).thenReturn(globalRatingToExpect);
        //Assert
        firestoreViewModel.receiveGlobalRating4ThisRestaurant().observeForever(globalRating -> {
            Assert.assertEquals(globalRatingToExpect.getValue(), globalRating);
        });
    }

    @Test
    public void receiveAllGlobalRatingsTest() {
        //Arrange
        MutableLiveData<List<GlobalRating>> globalRatingListToExpect = new MutableLiveData<>();
        globalRatingListToExpect.postValue(TestUtil.TEST_GLOBAL_RATING_LIST);
        //Act
        Mockito.when(firestoreRepository.receiveAllGlobalRatings()).thenReturn(globalRatingListToExpect);
        //Assert
        firestoreViewModel.receiveAllGlobalRatings().observeForever(globalRatings -> {
            Assert.assertArrayEquals(globalRatingListToExpect.getValue().toArray(), globalRatings.toArray());
        });
    }
}