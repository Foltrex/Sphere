package com.epam.ball.tests;

import com.epam.ball.data.DataReader;
import com.epam.ball.entity.Ball;
import com.epam.ball.entity.Point3D;
import com.epam.ball.exceptions.DataException;
import com.epam.ball.logic.BallCreator;
import com.epam.ball.logic.BallLineValidator;
import com.epam.ball.logic.Director;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DirectorTest {

    private static final String VALID_PATH = "correct_path";


    @Test
    public void testReadShouldReadWhenLinesCorrect() throws DataException {
        // given
        DataReader dataReader               = Mockito.mock(DataReader.class);
        Mockito.when(dataReader.read(VALID_PATH)).thenReturn(Arrays.asList("1.0  2.0  3.0  4.0", "5.0  6.0  7.0  8.0"));

        BallLineValidator ballLineValidator = Mockito.mock(BallLineValidator.class);
        Mockito.when(ballLineValidator.isValidLine(anyString())).thenReturn(true);

        BallCreator ballCreator             = Mockito.mock(BallCreator.class);
        Mockito.when(ballCreator.create(anyString())).thenReturn(Optional.of(new Ball(new Point3D(1, 2, 3), 4)),
                                                                 Optional.of(new Ball(new Point3D(5, 6, 7), 8)));

        Director director = new Director(dataReader, ballLineValidator, ballCreator);


        // when
        List<Ball> balls = new ArrayList<>();
        balls.add(new Ball(new Point3D(1, 2, 3), 4));
        balls.add(new Ball(new Point3D(5, 6, 7), 8));

        List<Ball> ballsRead = director.read(VALID_PATH);


        // then
        Assert.assertEquals(balls, ballsRead);

        verify(dataReader, times(1)).read(VALID_PATH);
        verify(ballLineValidator, times(2)).isValidLine(anyString());
        verify(ballCreator, times(2)).create(anyString());
        verifyNoMoreInteractions(dataReader, ballLineValidator, ballCreator);
    }

    @Test
    public void testReadShouldReadWhenSeveralLinesUncorrect() throws DataException {
        // given
        DataReader dataReader       = Mockito.mock(DataReader.class);
        Mockito.when(dataReader.read(VALID_PATH)).thenReturn(Arrays.asList("1.0  2.0  3.0  4.0", "5.0sdf  asd  7e3.452.5  8.0"));

        BallLineValidator ballLineValidator = Mockito.mock(BallLineValidator.class);
        Mockito.when(ballLineValidator.isValidLine(anyString())).thenReturn(true, false);

        BallCreator ballCreator     = Mockito.mock(BallCreator.class);
        Mockito.when(ballCreator.create("1.0  2.0  3.0  4.0")).thenReturn(Optional.of(new Ball(new Point3D(1, 2, 3), 4)));

        Director director = new Director(dataReader, ballLineValidator, ballCreator);


        // when
        List<Ball> balls = new ArrayList<>();
        balls.add(new Ball(new Point3D(1, 2, 3), 4));

        List<Ball> ballsRead = director.read(VALID_PATH);


        // then
        Assert.assertEquals(balls, ballsRead);

        verify(dataReader).read(VALID_PATH);
        verify(ballLineValidator, times(2)).isValidLine(anyString());
        verify(ballCreator, times(1)).create(anyString());
    }
}
