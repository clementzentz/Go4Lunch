package clement.zentz.go4lunch.viewModels;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.repository.FirestoreRepository;
import clement.zentz.go4lunch.util.TestUtil;

public class FirestoreViewModelTest {

    private Workmate WORKMATES2 = new Workmate(TestUtil.TEST_WORKMATE_2);

    //system under test
    private FirestoreViewModel mFirestoreViewModel;

    @Mock
    private FirestoreRepository mFirestoreRepository;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mFirestoreViewModel = new FirestoreViewModel(mFirestoreRepository);
    }

    @Test
    public void addOrUpdateUserToFirestore() {
        //Arrange

        //Act

        //Assert
    }
}