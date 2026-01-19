package com.lucasteixeira.bank.application.constraints;

import com.lucasteixeira.bank.application.constraints.annotations.Cpf;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

    @Override
    public void initialize(Cpf constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null) {
            return true;
        }

        String cpfLimpo = cpf.replaceAll("\\D", "");

        if (cpfLimpo.length() != 11 || isAllDigitsEqual(cpfLimpo)) {
            return false;
        }

        return calcularDigitos(cpfLimpo);
    }

    private boolean isAllDigitsEqual(String cpf) {
        char firstDigit = cpf.charAt(0);
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != firstDigit) {
                return false;
            }
        }
        return true;
    }

    private boolean calcularDigitos(String cpf) {
        try {
            int d1, d2;
            int digito1, digito2, resto;
            int digitoCPF;
            String nDigResult;

            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                int num = (int) (cpf.charAt(i) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            resto = 11 - (soma % 11);
            if ((resto == 10) || (resto == 11))
                digito1 = '0';
            else
                digito1 = (char) (resto + 48);

            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                int num = (int) (cpf.charAt(i) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            resto = 11 - (soma % 11);
            if ((resto == 10) || (resto == 11))
                digito2 = '0';
            else
                digito2 = (char) (resto + 48);

            return (digito1 == cpf.charAt(9)) && (digito2 == cpf.charAt(10));

        } catch (Exception e) {
            return false;
        }
    }
}
