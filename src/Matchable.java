public interface Matchable<T extends Card> {
    boolean matches(T other);
}
