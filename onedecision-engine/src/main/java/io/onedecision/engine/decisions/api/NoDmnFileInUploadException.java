/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package io.onedecision.engine.decisions.api;

/**
 * A very specific exception in response to an attempt to upload files not
 * containing any DMN.
 *
 * @author Tim Stephenson
 */
public class NoDmnFileInUploadException extends IllegalArgumentException{

    private static final long serialVersionUID = 3410221492413480890L;

    public static final String MESSAGE = "Expected one DMN file and optionally one image file but no file with a DMN extension was found";
    
    public NoDmnFileInUploadException() {
        super();
    }

}
