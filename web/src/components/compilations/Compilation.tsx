import Poster from '@/src/components/poster/Poster'
import useContentPageState from '@/src/state/contentPageState'
import Base64WithId from '@/src/types/Base64WithId'
import ContentMetadata from '@/src/types/ContentMetadata'
import { BlurView } from 'expo-blur'
import { useRouter } from 'expo-router'
import React, { ReactElement, useEffect, useRef, useState } from 'react'
import {
    Animated,
    FlatList,
    InteractionManager,
    ScrollView,
    Text,
    TouchableOpacity,
    View,
} from 'react-native'
import CompilationKind from './CompilationKind'

interface CompilationProps {
    postersWithIds: Base64WithId[]
    metadataList: ContentMetadata[]
    errmes?: string
    compilationKind?: CompilationKind
    gridCols?: number
}

const Compilation = ({
    postersWithIds,
    metadataList,
    errmes = 'постеры загружаются...',
    compilationKind = CompilationKind.DEFAULT,
    gridCols = 3,
}: CompilationProps): ReactElement => {
    const [focusedIndex, setFocusedIndex] = useState<number>(0)
    const [selectedIndex, setSelectedIndex] = useState<number>()

    const router = useRouter()

    const scaleAnim = useRef(new Animated.Value(1)).current

    const { setContentPageMetadata } = useContentPageState()

    const isGrid = compilationKind === CompilationKind.GRID

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
                onPress={() => {
                    setSelectedIndex(index)
                    setContentPageMetadata(metadata)
                    router.push('/content')
                }}
                onFocus={() => handleFocus(index)}
                onBlur={handleBlur}
            >
                <View
                    className={
                        `${isFocused || (isSelected && 'scale-150')} ` +
                        ` ${compilationKind === CompilationKind.DEFAULT && 'aspect-[2/3] w-[35vw] max-w-[220px]'} ` +
                        ` ${isGrid && 'mr-5 aspect-[2/3] w-[30vw] max-w-[180px]'} ` +
                        ` ${compilationKind === CompilationKind.HORIZONTAL && 'aspect-[16/9] min-w-[27vw]'} `
                    }
                >
                    <Poster
                        id={postersWithIds[index].id}
                        base64Image={postersWithIds[index].image}
                    />
                </View>
            </TouchableOpacity>
        )
    }

    return (
        <>
            <BlurView intensity={30} tint="prominent">
                {postersWithIds && postersWithIds.length !== 0 ? (
                    isGrid ? (
                        <ScrollView
                            className={
                                'flex w-full items-center justify-center'
                            }
                        >
                            <FlatList
                                horizontal={false}
                                numColumns={gridCols}
                                showsHorizontalScrollIndicator={false}
                                contentContainerStyle={{
                                    gap: 16,
                                    padding: 12,
                                    borderRadius: 10,
                                }}
                                data={metadataList}
                                keyExtractor={(item, index) =>
                                    item.id?.toString() ?? index.toString()
                                }
                                renderItem={({ item, index }) => (
                                    <SelectablePoster
                                        metadata={item}
                                        index={index}
                                    />
                                )}
                            />
                        </ScrollView>
                    ) : (
                        <FlatList
                            horizontal={true}
                            showsHorizontalScrollIndicator={false}
                            contentContainerStyle={{
                                flexDirection: 'row',
                                gap: 16,
                                padding: 12,
                                borderRadius: 10,
                            }}
                            data={metadataList}
                            keyExtractor={(item, index) =>
                                item.id?.toString() ?? index.toString()
                            }
                            renderItem={({ item, index }) => (
                                <SelectablePoster
                                    metadata={item}
                                    index={index}
                                />
                            )}
                        />
                    )
                ) : (
                    <Text
                        className={'text-1xl font-bold text-white opacity-20'}
                    >
                        {errmes}
                    </Text>
                )}
            </BlurView>
        </>
    )
}

export default Compilation
