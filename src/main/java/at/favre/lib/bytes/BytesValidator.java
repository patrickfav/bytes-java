/*
 * Copyright 2017 Patrick Favre-Bulle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package at.favre.lib.bytes;

import java.util.List;

import static at.favre.lib.bytes.BytesValidator.Logical.Operator.NOT;
import static at.favre.lib.bytes.BytesValidator.Logical.Operator.OR;

/**
 * Interface for validating byte arrays
 */
public interface BytesValidator {

    /**
     * Validates given byte array
     *
     * @param byteArrayToValidate array, must not be altered, only read
     * @return true if validation is successful, false otherwise
     */
    boolean validate(byte[] byteArrayToValidate);

    /**
     * Validates for specific array length
     */
    final class Length implements BytesValidator {
        enum Mode {
            SMALLER_OR_EQ_THAN, GREATER_OR_EQ_THAN, EXACT
        }

        private final int refLength;
        private final Mode mode;

        public Length(int refLength, Mode mode) {
            this.refLength = refLength;
            this.mode = mode;
        }

        @Override
        public boolean validate(byte[] byteArrayToValidate) {
            switch (mode) {
                case GREATER_OR_EQ_THAN:
                    return byteArrayToValidate.length >= refLength;
                case SMALLER_OR_EQ_THAN:
                    return byteArrayToValidate.length <= refLength;
                default:
                case EXACT:
                    return byteArrayToValidate.length == refLength;
            }
        }
    }

    /**
     * Checks if a byte array contains only the same value
     */
    final class IdenticalContent implements BytesValidator {
        private final byte refByte;

        enum Mode {
            ONLY_OF, NONE_OF, NOT_ONLY_OF
        }

        private final Mode mode;

        IdenticalContent(byte refByte, Mode mode) {
            this.refByte = refByte;
            this.mode = mode;
        }

        @Override
        public boolean validate(byte[] byteArrayToValidate) {
            for (byte b : byteArrayToValidate) {
                if (mode == Mode.NONE_OF && b == refByte) {
                    return false;
                }
                if (mode == Mode.ONLY_OF && b != refByte) {
                    return false;
                }
                if (mode == Mode.NOT_ONLY_OF && b != refByte) {
                    return true;
                }
            }
            return mode == Mode.NONE_OF || mode == Mode.ONLY_OF;
        }
    }

    /**
     * Checks if arrays end or start with given array
     */
    final class PrePostFix implements BytesValidator {

        private final byte[] pfix;
        private final boolean startsWith;

        public PrePostFix(boolean startsWith, byte... pfix) {
            this.pfix = pfix;
            this.startsWith = startsWith;
        }

        @Override
        public boolean validate(byte[] byteArrayToValidate) {
            if (pfix.length > byteArrayToValidate.length) {
                return false;
            }

            for (int i = 0; i < pfix.length; i++) {
                if (startsWith && pfix[i] != byteArrayToValidate[i]) {
                    return false;
                }
                if (!startsWith && pfix[i] != byteArrayToValidate[byteArrayToValidate.length - pfix.length + i]) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Logical operations over multiple validators
     */
    final class Logical implements BytesValidator {
        enum Operator {
            OR, AND, NOT
        }

        private final List<BytesValidator> validatorList;

        private final Operator operator;

        public Logical(List<BytesValidator> validatorList, Operator operator) {
            if (validatorList.isEmpty())
                throw new IllegalArgumentException("must contain at least 1 element");
            if (operator == NOT && validatorList.size() != 1)
                throw new IllegalArgumentException("not operator can only be applied to single element");
            this.validatorList = validatorList;
            this.operator = operator;
        }

        @Override
        public boolean validate(byte[] byteArrayToValidate) {
            if (operator == NOT) {
                return !validatorList.get(0).validate(byteArrayToValidate);
            }

            boolean bool = operator != OR;
            for (BytesValidator bytesValidator : validatorList) {
                switch (operator) {
                    case AND:
                        bool &= bytesValidator.validate(byteArrayToValidate);
                        break;
                    default:
                    case OR:
                        bool |= bytesValidator.validate(byteArrayToValidate);
                        break;
                }
            }
            return bool;
        }

    }
}
