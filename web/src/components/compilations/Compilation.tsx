import React, { ReactElement, useEffect, useRef, useState } from 'react'
import {
    Animated,
    FlatList,
    InteractionManager,
    Text,
    TouchableOpacity,
    View,
} from 'react-native'
import CompilationKind from './CompilationKind'
import Poster from '@/src/components/poster/Poster'
import ContentMetadata from '@/src/types/ContentMetadata'

interface CompilationProps {
    posterImageUrls: string[]
    metadataList: ContentMetadata[]
    errmes?: string
}

/**
 * @note posterImageUrls temporary image URLs array; each one can be inserted in the `<img src="...">` tag to display an image
 */
const Compilation = ({
    posterImageUrls,
    metadataList,
    errmes = 'постеры загружаются...',
}: CompilationProps): ReactElement => {
    const [focusedIndex, setFocusedIndex] = useState<number>(0)
    const [selectedIndex, setSelectedIndex] = useState<number>()

    const scaleAnim = useRef(new Animated.Value(1)).current

    const handleFocus = (index: number) => {
        Animated.timing(scaleAnim, {
            toValue: 1.1,
            duration: 200,
            useNativeDriver: true,
        }).start()
        setFocusedIndex(index)
    }

    const handleBlur = () => {
        Animated.timing(scaleAnim, {
            toValue: 1,
            duration: 200,
            useNativeDriver: true,
        }).start()
    }

    useEffect(() => {
        const listener = InteractionManager.createInteractionHandle()
        // TODO execute action on tv remote controller button press
        return () => InteractionManager.clearInteractionHandle(listener)
    }, [focusedIndex])

    interface SelectablePosterProps {
        metadata: ContentMetadata
        index: number
    }

    const SelectablePoster = ({
        metadata,
        index,
    }: SelectablePosterProps): ReactElement => {
        const isFocused = index === focusedIndex
        const isSelected = index === selectedIndex

        return (
            <TouchableOpacity
                activeOpacity={1}
                onPress={() => setSelectedIndex(index)}
                onFocus={() => handleFocus(index)}
                onBlur={handleBlur}
            >
                <View className={`${isFocused || (isSelected && 'scale-150')}`}>
                    <Poster
                        compilationKind={CompilationKind.PREVIEW}
                        metadata={metadata}
                        imageUrl={posterImageUrls[index]}
                    />
                </View>
            </TouchableOpacity>
        )
    }

    return (
        <>
            {posterImageUrls.length !== 0 ? (
                <FlatList
                    data={metadataList}
                    horizontal
                    keyExtractor={(item) => item.id} // Assuming id is a string
                    renderItem={({ item, index }) => (
                        <SelectablePoster metadata={item} index={index} />
                    )}
                    contentContainerStyle={{
                        flexDirection: 'row',
                        gap: 16,
                        padding: 12,
                        backgroundColor: 'white',
                    }}
                />
            ) : (
                <Text className={'text-1xl font-bold text-white opacity-20'}>
                    {errmes}
                </Text>
            )}
        </>
    )
}

export default Compilation
