/*
 * Copyright (c) 2020 - Present. Rochester Institute of Technology
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.rit.se.design.callgraph.analysis;

/**
 * Enumeration that selects which propagation algorithm to use.
 *
 * @author Joanna C. S. Santos
 * @author Reese A. Jones
 */
public class PointerAnalysisPolicy {
    public PolicyType policyType;
    /**
     * a value for the n or x in nCFA, ZeroXCFA, ZeroXContainerCFA
     */
    public int policyNumber;
    /**
     * @param policyNumber the bound n in the algorithms: n-CFA, 0-n-CFA, 0-n-Container-CFA.
     * @param policyType   the pointer analysis method (n-CFA, 0-n-CFA, 0-n-Container-CFA)
     */
    public PointerAnalysisPolicy(PolicyType policyType, int policyNumber) {
        this.policyType = policyType;
        this.policyNumber = policyNumber;
    }

    @Override
    public String toString() {
        switch (policyType) {
            case nCFA:
                return String.format("%d-CFA", policyNumber);
            case ZeroXCFA:
                return String.format("0-%d-CFA", policyNumber);
            case ZeroXContainerCFA:
                return String.format("0-%d-Container-CFA", policyNumber);
        }
        throw new IllegalArgumentException("Unknown value " + this);
    }


    public enum PolicyType {
        nCFA,
        ZeroXCFA,
        ZeroXContainerCFA;
    }
}

