/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.transformers

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.resolve.FirTypeResolver
import org.jetbrains.kotlin.fir.scopes.FirPosition
import org.jetbrains.kotlin.fir.scopes.FirScope
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.impl.FirResolvedFunctionTypeRefImpl
import org.jetbrains.kotlin.fir.types.impl.FirResolvedTypeRefImpl


class FirSpecificTypeResolverTransformer(
    private val towerScope: FirScope,
    private val position: FirPosition,
    override val session: FirSession
) : FirAbstractTreeTransformer(phase = FirResolvePhase.SUPER_TYPES) {
    override fun transformTypeRef(typeRef: FirTypeRef, data: Nothing?): FirTypeRef {
        val typeResolver = FirTypeResolver.getInstance(session)
        typeRef.transformChildren(FirSpecificTypeResolverTransformer(towerScope, FirPosition.OTHER, session), null)
        return transformType(typeRef, typeResolver.resolveType(typeRef, towerScope, position))
    }

    override fun transformFunctionTypeRef(functionTypeRef: FirFunctionTypeRef, data: Nothing?): FirTypeRef {
        val typeResolver = FirTypeResolver.getInstance(session)
        functionTypeRef.transformChildren(this, data)
        return FirResolvedFunctionTypeRefImpl(
            functionTypeRef.psi,
            typeResolver.resolveType(functionTypeRef, towerScope, position),
            functionTypeRef.isMarkedNullable,
            functionTypeRef.receiverTypeRef,
            functionTypeRef.returnTypeRef
        ).apply {
            annotations += functionTypeRef.annotations
            valueParameters += functionTypeRef.valueParameters
        }
    }

    private fun transformType(typeRef: FirTypeRef, resolvedType: ConeKotlinType): FirTypeRef {
        return FirResolvedTypeRefImpl(
            typeRef.psi,
            resolvedType
        ).apply {
            annotations += typeRef.annotations
        }
    }

    override fun transformResolvedTypeRef(resolvedTypeRef: FirResolvedTypeRef, data: Nothing?): FirTypeRef {
        return resolvedTypeRef
    }

    override fun transformImplicitTypeRef(implicitTypeRef: FirImplicitTypeRef, data: Nothing?): FirTypeRef {
        return implicitTypeRef
    }
}
