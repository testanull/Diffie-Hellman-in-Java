package Utilities;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DHUtil {

    private BigInteger p;
    private BigInteger g;
    private int bitLength;
    private BigInteger numberChoosen;
    private BigInteger valueCalculate;
    private BigInteger numberReceived;
    private BigInteger finalKey;

    public DHUtil() {
    }

    public DHUtil(BigInteger p, BigInteger g, int bitLength) {
        this.p = p;
        this.g = g;
        this.bitLength = bitLength;
    }

    public void chooseNumber() {
        SecureRandom rnd = new SecureRandom();
        do {
            numberChoosen = new BigInteger(getBitLength(), rnd);
        } while (numberChoosen.compareTo(p) >= 0);
    }

    public void calculateValueToSend() {
        valueCalculate = g.modPow(numberChoosen, p);
    }

    public void recvSideKey(BigInteger numberSended) {
        numberReceived = numberSended;
    }

    public void calculateFinalKey() {
        finalKey = numberReceived.modPow(numberChoosen, p);
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public BigInteger getNumberChoosen() {
        return numberChoosen;
    }

    public void setNumberChoosen(BigInteger numberChoosen) {
        this.numberChoosen = numberChoosen;
    }

    public int getBitLength() {
        return bitLength;
    }

    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }

    public BigInteger getValueCalculate() {
        return valueCalculate;
    }

    public void setValueCalculate(BigInteger valueCalculate) {
        this.valueCalculate = valueCalculate;
    }

    public BigInteger getNumberReceived() {
        return numberReceived;
    }

    public void setNumberReceived(BigInteger numberReceived) {
        this.numberReceived = numberReceived;
    }

    public BigInteger getFinalKey() {
        return finalKey;
    }

    public void setFinalKey(BigInteger finalKey) {
        this.finalKey = finalKey;
    }

}
