
public class argsObj {
    public String className;
    public String methName;
    public boolean isClass;
    public boolean isMethod;

    public argsObj(String cName, String mName, boolean isC, boolean isM) {
        this.className = cName;
        this.methName = mName;
        this.isClass = isC;
        this.isMethod = isM; // https://www.youtube.com/watch?v=4Ep6YVqc6Ks
    }
}