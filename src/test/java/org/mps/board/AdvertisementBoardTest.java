package org.mps.board;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdvertisementBoardTest {

    AdvertisementBoard ab;

    @BeforeEach
    public void setup()
    {
        ab = new AdvertisementBoard();
    }

    @AfterEach
    public void shutdown()
    {
        ab = null;
    }

    @Test
    @DisplayName("1. Comprobar que inicialmente hay un anuncio en el tablón.")
    void test1()
    {
        assertEquals(1, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("2. Crear un anuncio por parte de \"THE Company\", insertarlo y comprobar que se ha incrementado en uno el número de anuncios del tablón.")
    void test2()
    {
        Advertisement ad = new Advertisement("Second test", "Ad for the second test", "THE Company");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);

        ab.publish(ad, adDB, payment);

        assertEquals(2, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("3. Publicar un anuncio por parte del anunciante \"Pepe Gotera y Otilio\" y comprobar que, si está en la base de datos de anunciantes pero no tiene saldo, el anuncio no se inserta, lo que se determina observando que el número de anuncios no aumenta.")
    void test3()
    {
        Advertisement ad = new Advertisement("Third test", "Ad for the third test", "Pepe Gotera y Otilio");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);

        when(adDB.advertiserIsRegistered("Pepe Gotera y Otilio")).thenReturn(true);
        when(payment.advertiserHasFunds("Pepe Gotera y Otilio")).thenReturn(false);

        ab.publish(ad, adDB, payment);

        assertEquals(1, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("4. Publicar un anuncio por parte de un anunciante llamado \"Robin Robot\", asumiendo que está en la base de datos de anunciantes, que tiene saldo y finalmente comprobando que se ha realizado el cargo.")
    void test4()
    {
        Advertisement ad = new Advertisement("Fourth test", "Ad for the fourth test", "Robin Robot");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);

        when(adDB.advertiserIsRegistered("Robin Robot")).thenReturn(true);
        when(payment.advertiserHasFunds("Robin Robot")).thenReturn(true);
        payment.chargeAdvertiser("Robin Robot");
        verify(payment).chargeAdvertiser("Robin Robot");

        ab.publish(ad, adDB, payment);

        assertEquals(2, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("5. Publicar dos anuncios distintos por parte de \"THE Company\", borrar el primero y comprobar que si se busca ya no está en el tablón.")
    void test5()
    {
        Advertisement ad1 = new Advertisement("Fifth test 1", "First ad for the fifth test", "THE Company");
        Advertisement ad2 = new Advertisement("Fifth test 2", "Second ad for the fifth test", "THE Company");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);

        ab.publish(ad1, adDB, payment);
        ab.publish(ad2, adDB, payment);

        assertEquals(3, ab.numberOfPublishedAdvertisements());

        ab.deleteAdvertisement("Fifth test 1", "THE Company");

        Optional<Advertisement> result = ab.findByTitle("Fifth test 1");

        assertFalse(result.isPresent());
        assertEquals(2, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("6. Comprobar que si se intenta publicar un anuncio que ya existe (mismo título y mismo anunciante), no se realiza la publicación y no se realiza ningún cargo. El anunciante no debe ser \"THE Company\". Para pasar esta prueba hay que modificar la clase AdvertisementBoard.")
    void test6()
    {
        Advertisement ad = new Advertisement("Sixth test ad", "Ad for the sixth test", "NOT THE Company");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);
        when(adDB.advertiserIsRegistered("NOT THE Company")).thenReturn(true);
        when(payment.advertiserHasFunds("NOT THE Company")).thenReturn(true);

        ab.publish(ad, adDB, payment);
        ab.publish(ad, adDB, payment);

        assertEquals(2, ab.numberOfPublishedAdvertisements());
        verify(payment, times(1)).chargeAdvertiser("NOT THE Company");
    }

    @Test
    @DisplayName("7. Comprobar que si se intenta publicar un anuncio por parte del anunciante \"Tim O'Theo\" y el tablón está lleno se eleva la excepción AdvertisementBoardException.  Para pasar esta prueba hay que modificar la clase AdvertisementBoard.")
    void test7()
    {
        Advertisement ad = new Advertisement("Seventh test ad", "Ad for the seventh test", "Tim O'Theo");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);
        when(adDB.advertiserIsRegistered("Tim O'Theo")).thenReturn(true);
        when(payment.advertiserHasFunds("Tim O'Theo")).thenReturn(true);

        for (int i = 0; i < AdvertisementBoard.MAX_BOARD_SIZE; i++) {
            ab.publish(new Advertisement("Advertisement " + (i + 1), "Ad for the test", "NOT THE Company"), adDB, payment);
        }

        AdvertisementBoardException exception = assertThrows(AdvertisementBoardException.class, () -> ab.publish(ad, adDB, payment));
        assertEquals("Advertisement board is full", exception.getMessage());
        verify(adDB, never()).advertiserIsRegistered("Tim O'Theo");
        verify(payment, never()).advertiserHasFunds("Tim O'Theo");
        verify(payment, never()).chargeAdvertiser("Tim O'Theo");

    }
}
