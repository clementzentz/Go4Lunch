package clement.zentz.go4lunch.repository;

import org.junit.Before;
import org.junit.Test;

import clement.zentz.go4lunch.models.workmate.Workmate;
import clement.zentz.go4lunch.services.firestore.FirestoreApi;
import clement.zentz.go4lunch.util.TestUtil;
import static org.mockito.Mockito.mock;

class FirestoreRepositoryTest {

    private Workmate WORKMATE1 = new Workmate(TestUtil.TEST_WORKMATE_1);

    private FirestoreRepository mFirestoreRepository;

    private FirestoreApi mFirestoreApi;

    @Before
    public void initEach(){
        mFirestoreApi = mock(FirestoreApi.class);
        mFirestoreRepository = new FirestoreRepository();
    }

    @Test
    public void addOrUpdateCurrentUser_returnRow() throws Exception {
        //Arrange

        //Act

        //Assert
    }
}
