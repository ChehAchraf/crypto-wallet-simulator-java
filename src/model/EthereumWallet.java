package model;

public class EthereumWallet extends Wallet{

    public EthereumWallet(double amount) {
        super(amount,"ETHEREUM");
        super.setWalletAdresse(generateAdresse());
    }

    @Override
    public String getType() {
        return "ETHEREUM";
    }



    @Override
    public String generateAdresse() {
        return "eth-11";
    }
}
