package clement.zentz.go4lunch.repository;

import org.junit.BeforeClass;
import org.junit.Test;

import clement.zentz.go4lunch.services.firestore.FirestoreApi;

import static org.mockito.Mockito.mock;

class FirestoreRepositoryTest {

    private FirestoreRepositoryTest mFirestoreRepositoryTest;

    private FirestoreApi mFirestoreApi;

    @BeforeClass
    public void initEach(){
        mFirestoreApi = mock(FirestoreApi.class);
        mFirestoreRepositoryTest = new FirestoreRepositoryTest();
    }

    @Test
    public void insertWorkmate_returnRow() {

    }
}
