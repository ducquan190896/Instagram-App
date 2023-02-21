import { Alert, FlatList, Image, ListRenderItem, StyleSheet, Text, useWindowDimensions, View } from 'react-native'
import React, { useEffect, useRef, useState } from 'react'
import { useTailwind } from 'tailwind-rn/dist'
import { useDispatch, useSelector } from 'react-redux'
import { Button } from '@rneui/base'
import {Picker} from '@react-native-picker/picker';
import * as ImagePicker from 'expo-image-picker';
import PostCardDot from '../Components/PostCardDot'
import { createStoryAction, resetStoryAction } from '../Store/Actions/StoryAction'
import { RootState } from '../Store/Store'

const StoryCreateForm = () => {
    const [images, setImages] = useState<string[] | []>([])
    const [image, setImage] = useState<string | null>(null)
    const tw = useTailwind()
    const dispatch = useDispatch()
    const windownWith = useWindowDimensions().width
    const [activeIndex, setActiveIndex] = useState<number>(0)   
    const {stories, storySuccess, storyError} = useSelector((state: RootState) => state.STORIES)
    const [isAddImage, setIsAddImage] = useState<boolean>(true)

    const addImagesFunction = async () => {
        const results = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.Images,
            allowsMultipleSelection: true,
            selectionLimit: 3,
            quality: 1,
            aspect: [4, 3]
        })
        console.log(results.assets)

        const formdata = new FormData()
        let n = 0;
        if(!results.canceled) {
            while(n < results.assets.length) {
                const split = results.assets[n].uri.split("/")
                const fileNameFull = split[split.length - 1].split(".")
                const imageFile = {
                    uri: results.assets[n].uri,
                    type: results.assets[n].type + "/" + fileNameFull[1],
                    name: fileNameFull[0]
                }
                console.log(imageFile)
                await formdata.append("file" , JSON.parse(JSON.stringify(imageFile)))
                n++
            }
        }

        const res = await fetch("http://10.0.2.2:8080/api/images/uploadImages", {
            method: "POST",
            headers: {
                "Content-Type": "multipart/form-data"
            },
            body: formdata
        })

        const imageUrls = await res.json()

        console.log(imageUrls)
        setIsAddImage(false)
        setImages(imageUrls)
        // setIsAddImage(true)
    }
    const handleRenderItem: ListRenderItem<any> = ({item, index}) => (
        // <Image key={index}  style={[tw('text-base'), {width: windownWith, height: 300, resizeMode: 'cover'}]} source={item ? {uri:  "http://10.0.2.2:8080" + item}: require("../assets/download.png")}></Image>      
        <Image source={{uri:  "http://10.0.2.2:8080" + item}} style={[tw('text-base'), {width: windownWith, height: 400, resizeMode: 'cover'}]}></Image>   
    )

    const onViewableItemsChanged = useRef(({viewableItems, changed}: {viewableItems: any, changed: any}) => {
        
            if(viewableItems.length > 0) {
                setActiveIndex(viewableItems[0].index)
            }
    })

    

    const addStoryFunction = async () => {
        if(images && images.length > 0) {
            await dispatch(createStoryAction({imageUrls: images}) as any)
            Alert.alert("added story Successfully")
            setImages([])
        } else {
            Alert.alert("please upload images")
        }
       

    }
    
  return (
    <View style={tw('flex-1')}>
     
      <Button onPress={addImagesFunction} buttonStyle={tw('rounded-lg items-center my-2 mb-6')} title={isAddImage ? "add Images" : "change Images"} containerStyle={tw('w-1/3 mx-auto')}></Button>
        <View style={tw('flex items-center justify-center')}>
         {images && images.length > 0 && (
             <FlatList
             data={ images}
             horizontal={true}
             showsHorizontalScrollIndicator={false}
             snapToAlignment='center'
             decelerationRate='fast'
             snapToInterval={windownWith}
             viewabilityConfig={{
               viewAreaCoveragePercentThreshold: 50,
             }}
             onViewableItemsChanged={onViewableItemsChanged.current}
             keyExtractor={(item: string) => item}
             renderItem={handleRenderItem}
             >
       
             </FlatList>
        )}
          {images && images.length > 0 && (
         <PostCardDot activeIndex={activeIndex} arrayLength={images && images.length > 0 ?  images?.length : 0}></PostCardDot>
       )}
        </View>
     {images && images.length > 0 && (
           <Button onPress={addStoryFunction} buttonStyle={tw('rounded-lg items-center mt-4 mx-4')} title="Add Story"></Button>
     )}
    </View>
  )
}

export default StoryCreateForm

const styles = StyleSheet.create({})