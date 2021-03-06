/**
 * Copyright (c) 2019-present Acrolinx GmbH
 */

package com.acrolinx.client.sdk.check;

/**
 * <p>
 * How to choose a correct Check Type? Following are different use cases of the check with
 * corresponding check type:
 * <table summary="" border="1">
 * <tr>
 * <td>Check Type</td>
 * <td>Ux</td>
 * <td>Use case</td>
 * </tr>
 * <tr>
 * <td>interactive</td>
 * <td>Interactive</td>
 * <td>User checks her own document.</td>
 * </tr>
 * <tr>
 * <td>batch</td>
 * <td>Interactive</td>
 * <td>User checks set of her own documents.</td>
 * </tr>
 * <tr>
 * <td>baseline</td>
 * <td>Noninteractive</td>
 * <td>A repository of documents are checked as part of build job running nightly and the user
 * doesn't own the documents.</td>
 * </tr>
 * <tr>
 * <td>automated</td>
 * <td>Noninteractive</td>
 * <td>Check of a single document for automated scenarios as for example a git hook or save a
 * document.</td>
 * </tr>
 * </table>
 *
 */
public enum CheckType
{
    batch, interactive, baseline, automated
}
