
package torok.gabriel.vizsga;

public class DomainValidationException {
    
    public class DomainValidationException extends Exception
    {
        public DomainValidationException(String massage)
        {
            super(massage);
        }
    }
}
