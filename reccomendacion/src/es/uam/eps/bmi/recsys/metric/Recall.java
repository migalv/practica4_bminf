/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recsys.metric;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

/**
 *
 * @author sergio
 */
public class Recall implements Metric {

    private final Ratings ratings;
    private final double threshold;
    private final int cutoff;

    public Recall(Ratings test, double threshold, int cutoff) {
        this.ratings = test;
        this.threshold = threshold;
        this.cutoff = cutoff;
    }

    @Override
    public double compute(Recommendation rec) {
        double precisionParcial = 0, precisionFinal, userCounter = 0;

        for (int current_user : rec.getUsers()) {
            int counterRefactor = 0, positiveUser = 0, positiveItem = 0;

            if (this.ratings.getUsers().contains(current_user)) {
                positiveItem = this.ratings.getItems(current_user).stream().filter((current_item) -> (this.ratings.getItems(current_user).contains(current_item))).map((current_item) -> this.ratings.getRating(current_user, current_item)).filter((scoreParcial) -> (scoreParcial > this.threshold)).map((_item) -> 1).reduce(positiveItem, Integer::sum);
            }

            //positiveItem = this.ratings.getItems(current_user).stream().filter((current_item) -> (this.ratings.getRating(current_user, current_item) >= this.threshold)).map((_item) -> 1).reduce(positiveItem, Integer::sum);
            if (rec.getRecommendation(current_user).size() > 0) {
                userCounter++;
                for (RankingElement rankingRec : rec.getRecommendation(current_user)) {
                    Double scoreParcial = this.ratings.getRating(current_user, rankingRec.getID());

                    if (scoreParcial != null && scoreParcial > this.threshold) {
                        positiveUser++;
                    }

                    counterRefactor++;
                    if (counterRefactor == this.cutoff) {
                        break;
                    }
                }
            }

            if (positiveUser > 0) {
                precisionParcial += ((double) positiveUser / positiveItem);
            }

        }

        precisionFinal = ((double) precisionParcial / userCounter);

        return precisionFinal;

    }

    @Override
    public String toString() {
        return "Recall" + "@" + this.cutoff;
    }

}
