from __future__ import print_function
__author__ = 'Yashraj'

import os

import numpy as np
from skimage import io
import scipy.misc as sp
from skimage import transform



# num_categories = 1

# batch_size = 1000

categories = ['airplane',    'alarm clock',    'angel',    'ant',    'apple',    'arm',    'armchair',    'ashtray',    'axe',    'backpack',    'banana',    'barn',    'baseball bat',    'basket',    'bathtub',    'bear (animal)',    'bed',    'bee',    'beer-mug',    'bell',    'bench',    'bicycle',    'binoculars',    'blimp',    'book',    'bookshelf',    'boomerang',    'bottle opener',    'bowl',    'brain',    'bread',    'bridge',    'bulldozer',    'bus',    'bush',    'butterfly',    'cabinet',    'cactus',    'cake',    'calculator',    'camel',    'camera',    'candle',    'cannon',    'canoe',    'car (sedan)',    'carrot',    'castle',    'cat',    'cell phone',    'chair',    'chandelier',    'church',    'cigarette',    'cloud',    'comb',    'computer monitor',    'computer-mouse',    'couch',    'cow',    'crab',    'crane (machine)',    'crocodile',    'crown',    'cup',    'diamond',    'dog',    'dolphin',    'donut',    'door',    'door handle',    'dragon',    'duck',    'ear',    'elephant',    'envelope',    'eye',    'eyeglasses',    'face',    'fan',    'feather',    'fire hydrant',    'fish',    'flashlight',    'floor lamp',    'flower with stem',    'flying bird',    'flying saucer',    'foot',    'fork',    'frog',    'frying-pan',    'giraffe',    'grapes',    'grenade',    'guitar',    'hamburger',    'hammer',    'hand',    'harp',    'hat',    'head',    'head-phones',    'hedgehog',    'helicopter',    'helmet',    'horse',    'hot air balloon',    'hot-dog',    'hourglass',    'house',    'human-skeleton',    'ice-cream-cone',    'ipod',    'kangaroo',    'key',    'keyboard',    'knife',    'ladder',    'laptop',    'leaf',    'lightbulb',    'lighter',    'lion',    'lobster',    'loudspeaker',    'mailbox',    'megaphone',    'mermaid',    'microphone',    'microscope',    'monkey',    'moon',    'mosquito',    'motorbike',    'mouse (animal)',    'mouth',    'mug',    'mushroom',    'nose',    'octopus',    'owl',    'palm tree',    'panda',    'paper clip',    'parachute',    'parking meter',    'parrot',    'pear',    'pen',    'penguin',    'person sitting',    'person walking',    'piano',    'pickup truck',    'pig',    'pigeon',    'pineapple',    'pipe (for smoking)',    'pizza',    'potted plant',    'power outlet',    'present',    'pretzel',    'pumpkin',    'purse',    'rabbit',    'race car',    'radio',    'rainbow',    'revolver',    'rifle',    'rollerblades',    'rooster',    'sailboat',    'santa claus',    'satellite',    'satellite dish',    'saxophone',    'scissors',    'scorpion',    'screwdriver',    'sea turtle',    'seagull',    'shark',    'sheep',    'ship',    'shoe',    'shovel',    'skateboard',    'skull',    'skyscraper',    'snail',    'snake',    'snowboard',    'snowman',    'socks',    'space shuttle',    'speed-boat',    'spider',    'sponge bob',    'spoon',    'squirrel',    'standing bird',    'stapler',    'strawberry',    'streetlight',    'submarine',    'suitcase',    'sun',    'suv',    'swan',    'sword',    'syringe',    't-shirt',    'table',    'tablelamp',    'teacup',    'teapot',    'teddy-bear',    'telephone',    'tennis-racket',    'tent',    'tiger',    'tire',    'toilet',    'tomato',    'tooth',    'toothbrush',    'tractor',    'traffic light',    'train',    'tree',    'trombone',    'trousers',    'truck',    'trumpet',    'tv',    'umbrella',    'van',    'vase',    'violin',    'walkie talkie',    'wheel',    'wheelbarrow',    'windmill',    'wine-bottle',    'wineglass',    'wrist-watch',    'zebra']


def load_dataset_with_channels(num_categories):

    filename = 'sketch_data' + str(num_categories) + '.npz'
    if os.path.exists(filename):
        print("Loading dataset from file...")
        X = np.load(filename)
        return X['arr_0'], X['arr_1'], X['arr_2'], X['arr_3'], X['arr_4'], X['arr_5']


    data = []
    data_test = []
    y_train = []
    y_test = []
    i = 1
    fold = 0.1*80
    for cat in range(0, num_categories): #len(categories) # a model was save with 20 classes

        for k in range(0, 80):
            img = io.imread('data/png/' + str(categories[cat]) + '/' + str(i) + '.png',as_grey=True)
            img = sp.imresize(img, (224, 224)) # resize according to model
            # stack across channels
            img = img[:, :, np.newaxis]
            img = np.repeat(img, 3, axis=2)
            img = np.swapaxes(img, 2, 0)
            img = img.flatten() #reshape((9216))
            img = np.abs(img - np.mean(img))
            if k < 80 - fold:
                data.append(img)
                y_train.append(cat)
            else:
                data_test.append(img)
                y_test.append(cat)

            '''
            Experimental Congealing and Data Augmentation for closer to having One-Shot Learning

            img2 = imgflip.reshape((9216))
            data.append(img2)
            y_train.append(cat)
            '''

            i += 1

    data = np.array(data)


    X_train = data
    y_train = np.array(y_train)

    X_val = np.array(data_test)
    y_val = np.array(y_test)
    X_test = X_train[0:fold]
    y_test = y_train[0:fold]
    # X_val, y_val = data[1]
    # X_test, y_test = data[2]

    # The inputs come as vectors, we reshape them to monochrome 2D images,
    # according to the shape convention: (examples, channels, rows, columns)
    X_train = X_train.reshape((-1, 3, 224, 224))
    X_val = X_val.reshape((-1, 3, 224, 224))
    X_test = X_test.reshape((-1, 3, 224, 224))

    # The targets are int64, we cast them to int8 for GPU compatibility.
    y_train = y_train.astype(np.uint8)
    y_val = y_val.astype(np.uint8)
    y_test = y_test.astype(np.uint8)

    # We just return all the arrays in order, as expected in main().
    # (It doesn't matter how we do this as long as we can read them again.)
    np.savez_compressed('sketch_data' + str(num_categories) + '.npz', X_train, y_train, X_val, y_val, X_test, y_test)
    return X_train, y_train, X_val, y_val, X_test, y_test

# X_train, y_train, X_val, y_val, X_test, y_test = load_dataset()
# transformed = X_val


def rotate(img):
    return transform.rotate(img,60)


def random_rotate(img, lowerAngle=-10, upperAngle=10):
    from skimage import transform
    import random
    angle = random.randint(lowerAngle, upperAngle)
    return transform.rotate(img,angle)


def flip(img):
    return np.fliplr(img)


def augment_batch(batch_array, random):
    for i in range(0, batch_array.shape[0]):
        if random.random() > 0.5:
            img = batch_array[i]
        # img has shape (3, 224, 224)
            img = np.swapaxes(img, 0, 2)
        # img has shape (224, 224, 3)
            #img = img.astype('uint8')
            img = random_rotate(img)
            img = np.swapaxes(img, 2, 0)
            batch_array[i] = img

        if random.random() > 0.5:
            img = batch_array[i]
        # img has shape (3, 224, 224)
            img = np.swapaxes(img, 0, 2)
        # img has shape (224, 224, 3)
            #img = img.astype('uint8')
            img = flip(img)
            img = np.swapaxes(img, 2, 0)
            batch_array[i] = img

    return batch_array

# import random as rnd
#
# transformed = augment_batch(transformed, random=rnd)
# img = transformed[0]
#     # img has shape (3, 224, 224)
# img = np.swapaxes(img, 0, 2)
#     # img has shape (224, 224, 3)
# img = img.astype('uint8')
#
# io.imsave('transformed.jpg', img)
# # for i in range(0, transformed.shape[0]):
# #     img = transformed[i]
# #     # img has shape (3, 224, 224)
# #     img = np.swapaxes(img, 0, 2)
# #     # img has shape (224, 224, 3)
# #     img = img.astype('uint8')
# #     #img = random_rotate(img)
# #
# #     io.imsave('transformed.jpg', img)
# #     break
#
# print('Saved and terminated')