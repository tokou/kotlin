/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.transformers

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.resolve.FirSymbolProvider
import org.jetbrains.kotlin.fir.resolve.firSymbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassifierSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeAliasSymbol
import org.jetbrains.kotlin.fir.types.ConeLookupTagBasedType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.visitors.FirDefaultVisitor
import org.jetbrains.kotlin.fir.visitors.FirTransformer

import org.jetbrains.kotlin.name.ClassId

class FirSealedClassInheritorsTransformer : FirTransformer<Nothing?>() {
    override fun <E : FirElement> transformElement(element: E, data: Nothing?): E {
        throw IllegalStateException("Should not be there")
    }

    override fun transformFile(file: FirFile, data: Nothing?): FirDeclaration {
        val sealedClassInheritorsMap = mutableMapOf<FirSealedClass, MutableList<ClassId>>()
        file.accept(InheritorsCollector, sealedClassInheritorsMap)
        if (sealedClassInheritorsMap.isEmpty()) return file
        return file.transform(InheritorsTransformer(sealedClassInheritorsMap), null)
    }

    private class InheritorsTransformer(private val inheritorsMap: MutableMap<FirSealedClass, MutableList<ClassId>>) : FirTransformer<Nothing?>() {
        override fun <E : FirElement> transformElement(element: E, data: Nothing?): E {
            return element
        }

        override fun transformFile(file: FirFile, data: Nothing?): FirDeclaration {
            return (file.transformChildren(this, data) as FirFile)
        }

        override fun transformRegularClass(regularClass: FirRegularClass, data: Nothing?): FirStatement {
            if (inheritorsMap.isEmpty()) return regularClass
            return (regularClass.transformChildren(this, data) as FirRegularClass)
        }

        override fun transformSealedClass(sealedClass: FirSealedClass, data: Nothing?): FirStatement {
            val inheritors = inheritorsMap.remove(sealedClass)
            if (inheritors != null) {
                sealedClass.replaceInheritors(inheritors)
            }
            return transformRegularClass(sealedClass, data)
        }
    }

    private object InheritorsCollector : FirDefaultVisitor<Unit, MutableMap<FirSealedClass, MutableList<ClassId>>>() {
        override fun visitElement(element: FirElement, data: MutableMap<FirSealedClass, MutableList<ClassId>>) {}

        override fun visitFile(file: FirFile, data: MutableMap<FirSealedClass, MutableList<ClassId>>) {
            file.declarations.forEach { it.accept(this, data) }
        }

        override fun visitRegularClass(regularClass: FirRegularClass, data: MutableMap<FirSealedClass, MutableList<ClassId>>) {
            regularClass.declarations.forEach { it.accept(this, data) }

            val symbolProvider = regularClass.session.firSymbolProvider

            for (typeRef in regularClass.superTypeRefs) {
                val parent = extractClassFromTypeRef(symbolProvider, typeRef).takeIf { it?.modality == Modality.SEALED } ?: continue
                val inheritors = data.computeIfAbsent(parent as FirSealedClass) { mutableListOf() }
                inheritors += regularClass.symbol.classId
            }
        }

        private fun extractClassFromTypeRef(symbolProvider: FirSymbolProvider, typeRef: FirTypeRef): FirRegularClass? {
            val lookupTag = ((typeRef as FirResolvedTypeRef).type as? ConeLookupTagBasedType)?.lookupTag ?: return null
            val classLikeSymbol: FirClassifierSymbol<*> = symbolProvider.getSymbolByLookupTag(lookupTag) ?: return null
            return when (classLikeSymbol) {
                is FirClassSymbol -> classLikeSymbol.fir
                is FirTypeAliasSymbol -> extractClassFromTypeRef(symbolProvider, classLikeSymbol.fir.expandedTypeRef)
                else -> null
            }
        }
    }
}