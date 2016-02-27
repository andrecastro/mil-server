package br.edu.ifce.ppd.tria.server.business.builder;

import br.edu.ifce.ppd.tria.core.model.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Arrays.asList;

/**
 * Created by andrecoelho on 2/23/16.
 */
public class BoardBuilder {

//    Board with respectively spot ids
//    1---------10---------22
//    |         |          |
//    |  4------11------19 |
//    |  |      |       |  |
//    |  |  7---12---16 |  |
//    |  |  |        |  |  |
//    2--5--8        17-20-23
//    |  |  |        |  |  |
//    |  |  9---13---18 |  |
//    |  |      |       |  |
//    |  6------14------21 |
//    |         |          |
//    3---------15---------24
    public static HashMap<Integer, Spot> buildNewBoard() {
        Spot spot1 = new Spot(1);
        Spot spot2 = new Spot(2);
        Spot spot3 = new Spot(3);
        Spot spot4 = new Spot(4);
        Spot spot5 = new Spot(5);
        Spot spot6 = new Spot(6);
        Spot spot7 = new Spot(7);
        Spot spot8 = new Spot(8);
        Spot spot9 = new Spot(9);
        Spot spot10 = new Spot(10);
        Spot spot11 = new Spot(11);
        Spot spot12 = new Spot(12);
        Spot spot13 = new Spot(13);
        Spot spot14 = new Spot(14);
        Spot spot15 = new Spot(15);
        Spot spot16 = new Spot(16);
        Spot spot17 = new Spot(17);
        Spot spot18 = new Spot(18);
        Spot spot19 = new Spot(19);
        Spot spot20 = new Spot(20);
        Spot spot21 = new Spot(21);
        Spot spot22 = new Spot(22);
        Spot spot23 = new Spot(23);
        Spot spot24 = new Spot(24);

        spot1.addPossiblePath(asList(spot2, spot10));
        spot2.addPossiblePath(asList(spot1, spot3, spot5));
        spot3.addPossiblePath(asList(spot2, spot15));
        spot4.addPossiblePath(asList(spot5, spot11));
        spot5.addPossiblePath(asList(spot2, spot4, spot6, spot8));
        spot6.addPossiblePath(asList(spot5, spot14));
        spot7.addPossiblePath(asList(spot8, spot12));
        spot8.addPossiblePath(asList(spot5, spot7, spot9));
        spot9.addPossiblePath(asList(spot8, spot13));
        spot10.addPossiblePath(asList(spot1, spot11, spot22));
        spot11.addPossiblePath(asList(spot4, spot10, spot12, spot19));
        spot12.addPossiblePath(asList(spot7, spot11, spot16));
        spot13.addPossiblePath(asList(spot9, spot14, spot18));
        spot14.addPossiblePath(asList(spot6, spot13, spot15, spot21));
        spot15.addPossiblePath(asList(spot3, spot14, spot24));
        spot16.addPossiblePath(asList(spot12, spot17));
        spot17.addPossiblePath(asList(spot16, spot18, spot20));
        spot18.addPossiblePath(asList(spot13, spot17));
        spot19.addPossiblePath(asList(spot11, spot20));
        spot20.addPossiblePath(asList(spot17, spot19, spot21, spot23));
        spot21.addPossiblePath(asList(spot14, spot20));
        spot22.addPossiblePath(asList(spot10, spot23));
        spot23.addPossiblePath(asList(spot20, spot22, spot24));
        spot24.addPossiblePath(asList(spot15, spot23));

        Mil mil1 = new Mil(1);
        mil1.setSpots(new ArrayList<>(asList(spot1, spot2, spot3)));

        Mil mil2 = new Mil(2);
        mil2.setSpots(new ArrayList<>(asList(spot4, spot5, spot6)));

        Mil mil3 = new Mil(3);
        mil3.setSpots(new ArrayList<>(asList(spot7, spot8, spot9)));

        Mil mil4 = new Mil(4);
        mil4.setSpots(new ArrayList<>(asList(spot10, spot11, spot12)));

        Mil mil5 = new Mil(5);
        mil5.setSpots(new ArrayList<>(asList(spot13, spot14, spot15)));

        Mil mil6 = new Mil(6);
        mil6.setSpots(new ArrayList<>(asList(spot16, spot17, spot18)));

        Mil mil7 = new Mil(7);
        mil7.setSpots(new ArrayList<>(asList(spot19, spot20, spot21)));

        Mil mil8 = new Mil(8);
        mil8.setSpots(new ArrayList<>(asList(spot22, spot23, spot24)));

        Mil mil9 = new Mil(9);
        mil9.setSpots(new ArrayList<>(asList(spot1, spot10, spot22)));

        Mil mil10 = new Mil(10);
        mil10.setSpots(new ArrayList<>(asList(spot4, spot11, spot19)));

        Mil mil11 = new Mil(11);
        mil11.setSpots(new ArrayList<>(asList(spot7, spot12, spot16)));

        Mil mil12 = new Mil(12);
        mil12.setSpots(new ArrayList<>(asList(spot2, spot5, spot8)));

        Mil mil13 = new Mil(13);
        mil13.setSpots(new ArrayList<>(asList(spot17, spot20, spot23)));

        Mil mil14 = new Mil(14);
        mil14.setSpots(new ArrayList<>(asList(spot9, spot13, spot18)));

        Mil mil15 = new Mil(15);
        mil15.setSpots(new ArrayList<>(asList(spot6, spot14, spot21)));

        Mil mil16 = new Mil(16);
        mil16.setSpots(new ArrayList<>(asList(spot3, spot15, spot24)));

        spot1.setMilsBelongsTo(new ArrayList<>(asList(mil1, mil9)));
        spot2.setMilsBelongsTo(new ArrayList<>(asList(mil1, mil12)));
        spot3.setMilsBelongsTo(new ArrayList<>(asList(mil1, mil16)));

        spot4.setMilsBelongsTo(new ArrayList<>(asList(mil2, mil10)));
        spot5.setMilsBelongsTo(new ArrayList<>(asList(mil2, mil12)));
        spot6.setMilsBelongsTo(new ArrayList<>(asList(mil2, mil15)));

        spot7.setMilsBelongsTo(new ArrayList<>(asList(mil3, mil11)));
        spot8.setMilsBelongsTo(new ArrayList<>(asList(mil3, mil12)));
        spot9.setMilsBelongsTo(new ArrayList<>(asList(mil3, mil14)));

        spot10.setMilsBelongsTo(new ArrayList<>(asList(mil4, mil9)));
        spot11.setMilsBelongsTo(new ArrayList<>(asList(mil4, mil10)));
        spot12.setMilsBelongsTo(new ArrayList<>(asList(mil4, mil11)));

        spot13.setMilsBelongsTo(new ArrayList<>(asList(mil5, mil14)));
        spot14.setMilsBelongsTo(new ArrayList<>(asList(mil5, mil15)));
        spot15.setMilsBelongsTo(new ArrayList<>(asList(mil5, mil16)));

        spot16.setMilsBelongsTo(new ArrayList<>(asList(mil6, mil11)));
        spot17.setMilsBelongsTo(new ArrayList<>(asList(mil6, mil13)));
        spot18.setMilsBelongsTo(new ArrayList<>(asList(mil6, mil14)));

        spot19.setMilsBelongsTo(new ArrayList<>(asList(mil7, mil10)));
        spot20.setMilsBelongsTo(new ArrayList<>(asList(mil7, mil3)));
        spot21.setMilsBelongsTo(new ArrayList<>(asList(mil7, mil15)));

        spot22.setMilsBelongsTo(new ArrayList<>(asList(mil8, mil9)));
        spot23.setMilsBelongsTo(new ArrayList<>(asList(mil8, mil13)));
        spot24.setMilsBelongsTo(new ArrayList<>(asList(mil8, mil16)));

        HashMap<Integer, Spot> board = new HashMap<>();
        board.put(1, spot1);
        board.put(2, spot2);
        board.put(3, spot3);
        board.put(4, spot4);
        board.put(5, spot5);
        board.put(6, spot6);
        board.put(7, spot7);
        board.put(8, spot8);
        board.put(9, spot9);
        board.put(10, spot10);
        board.put(11, spot11);
        board.put(12, spot12);
        board.put(13, spot13);
        board.put(14, spot14);
        board.put(15, spot15);
        board.put(16, spot16);
        board.put(17, spot17);
        board.put(18, spot18);
        board.put(19, spot19);
        board.put(20, spot20);
        board.put(21, spot21);
        board.put(22, spot22);
        board.put(23, spot23);
        board.put(24, spot24);

        return board;
    }
}
