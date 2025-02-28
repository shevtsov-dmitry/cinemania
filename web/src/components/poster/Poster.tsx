import { memo, ReactElement, useEffect } from 'react'

import ContentMetadata from '@/src/types/ContentMetadata'
import { Image, View } from 'react-native'
import CompilationKind from '../compilations/CompilationKind'
import useContentPageState from '@/src/state/contentPageState'
import { useRouter } from 'expo-router'

interface PosterProps {
    compilationKind: CompilationKind
    imageUrl: string
}

/**
 * @note image URL can be inserted in the `<img src="...">` tag to display image
 */
const Poster = ({
    compilationKind = CompilationKind.DEFAULT,
    imageUrl,
}: PosterProps): ReactElement => {
    const { setContentPageMetadata, showContentPage } = useContentPageState()

    return (
        <View
            className={`${compilationKind === CompilationKind.PREVIEW && 'h-[200px] w-[150px]'} ${compilationKind === CompilationKind.DEFAULT && 'h-96 w-64'} `}
        >
            <Image
                className={`h-full w-full rounded-3xl`}
                source={{ uri: imageUrl }}
            />
        </View>
    )
}

//   {/*<div className="postersInfo">*/}
//   {/*    {isPosterHovered && (*/}
//   {/*        <div>*/}
//   {/*            <ContentInformation/>*/}
//   {/*            <div className="flex w-64 justify-center">*/}
//   {/*                /!* <Link *!/*/}
//   {/*                /!*     className="absolute bottom-0 z-10 mb-10" *!/*/}
//   {/*                /!*     to={`/watch/${metadata.videoId}`} *!/*/}
//   {/*                /!* > *!/*/}
//   {/*                <button*/}
//   {/*                    className="select-none rounded-3xl bg-pink-700 p-4 font-sans text-2xl font-bold text-white opacity-75 hover:opacity-95"*/}
//   {/*                    // onClick={() => {};*/}
//   {/*                    //     // TODO rewrite to Zustand*/}
//   {/*                    //     // dispatch(setVideoId(metadata.videoId))*/}
//   {/*                    // }*/}
//   {/*                >*/}
//   {/*                    Смотреть*/}
//   {/*                </button>*/}
//   {/*                /!* </Link> *!/*/}
//   {/*            </div>*/}
//   {/*        </div>*/}
//   {/*    )}*/}
//   {/*</div>*/}
// {/* </Image> */}

export default Poster
