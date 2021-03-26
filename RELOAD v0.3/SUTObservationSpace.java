package org.deeplearning4j.rl4j.examples.advanced.DQN1;

import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;



public final class SUTObservationSpace implements ObservationSpace<QualityMeasures> {

        int maxResposeTimeThreshold;
        double maxErrorRateThreshold;

   public SUTObservationSpace(int maxResposeTimeThreshold, double maxErrorRateThreshold){
       this.maxResposeTimeThreshold = maxResposeTimeThreshold;
       this.maxErrorRateThreshold = maxErrorRateThreshold;
   }

    @Override
    public String getName() {
    return "Quality Measures";
}

    @Override
    public int[] getShape() {
        return new int[] {2};
    }

    @Override
    public INDArray getLow() {
        INDArray low = Nd4j.create(new float[] {0, 0});
        return low;
    }

    @Override
    public INDArray getHigh() {
        INDArray high = Nd4j.create(new float[] {maxResposeTimeThreshold, (float) maxErrorRateThreshold});
        return high;
    }

    /*
    public QualityMeasures getObservation() {
        return QM_state;
    }
*/

       /*
        public MalmoBox getObservation(WorldState world_state) {
        TimestampedStringVector observations = world_state.getObservations();

        if (observations.isEmpty())
            return null;

        String obs_text = observations.get((int) (observations.size() - 1)).getText();

        JSONObject observation = new JSONObject(obs_text);

        double xpos = observation.getDouble("XPos");
        double ypos = observation.getDouble("YPos");
        double zpos = observation.getDouble("ZPos");
        double yaw = observation.getDouble("Yaw");
        double pitch = observation.getDouble("Pitch");

        return new MalmoBox(xpos, ypos, zpos, yaw, pitch);
    }
        */

}


/*
public final class SUTState implements Encodable {
    private final QualityMeasures qualityMeasures;
    private final int step;

    public double[] toArray() {
        return this.qualityMeasures.toArray();
    }

    public SUTState(QualityMeasures qualityMeasures, int step) {
        this.qualityMeasures = qualityMeasures;
        this.step = step;
    }

    public QualityMeasures getQualityMeasures() {
        return this.qualityMeasures;
    }

    public int getStep() {
        return this.step;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SUTState)) {
            return false;
        } else {
            SUTState other = (SUTState) o;
            if (!Arrays.equals(this.toArray(), other.toArray())) {
                return false;
            } else {
                return this.getStep() == other.getStep();
            }
        }
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        int result = result * 59 + Arrays.hashCode(this.getValues());
        result = result * 59 + this.getStep();
        return result;
    }

    public String toString() {
        return "SUTState(" + qualityMeasures.toString() + "\n step:  " + this.getStep() + ")";
    }

}
*/
