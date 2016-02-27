__author__ = 'Yashraj'
from skimage import data
from skimage import transform as tf
from skimage.feature import (match_descriptors, corner_harris,
                             corner_peaks, ORB, plot_matches)
from skimage import io
from skimage.color import rgb2gray

import matplotlib.pyplot as plt

from skdata.mnist.views import OfficialImageClassification
from tsne import bh_sne
import numpy as np
import scipy.misc as sp
from os import walk
import theano.tensor as T

from os import walk

import zerorpc

num_keypoints = 2000  # add more to get each feature point lines to be drawn in DrawingApprentice
pathToImgLib = 'data/png/'

def TSNE(category, path2newimage):
    pathToCategory = pathToImgLib + str(category) + '/'

    for (dirpath, dirnames, filenames) in walk(pathToCategory):

        data = []
        data_test = []
        y_train = []
        y_test = []
        i = 1
        fold = 0.1*80

        for filename in filenames:
            if filename.index('.') == 0:
                continue
            img = io.imread(pathToCategory + filename)
            from skimage import color
            from skimage.feature import hog

            img = color.rgb2grey(img)

            img = sp.imresize(img, (126, 126))
            fd, hog_image = hog(img, orientations=8, pixels_per_cell=(16, 16), cells_per_block=(1, 1), visualise=True)

           # imgflip = np.fliplr(img)

            img = img.flatten() #reshape((9216))
            img = np.abs(img - np.mean(img))
            data.append(fd)
            y_train.append(i)
            i += 1

        from matplotlib.pyplot import imsave
        img = io.imread(path2newimage)
        from skimage import color

        img = color.rgb2grey(img)

        img = sp.imresize(img, (126, 126))

        fd, hog_image = hog(img, orientations=8, pixels_per_cell=(16, 16), cells_per_block=(1, 1), visualise=True)

        img = img.flatten() #reshape((9216))
        img = np.abs(img - np.mean(img))
        y_train.append(99)
        data.append(fd)

        data = np.array(data,dtype='float64')
        # print(x_data.shape)
        # print(data.shape)
        # perform t-SNE embedding
        vis_data = bh_sne(data,pca_d=None,d=2,perplexity=5,theta=0.9)

        # vis_data = bh_sne(vis_data)
        # vis_data = bh_sne(vis_data)
        # vis_data = bh_sne(vis_data)

        # plot the result
        vis_x = vis_data[:, 0]
        vis_y = vis_data[:, 1]

        #output = []
        #print vis_data, y_data
        # for i in range(vis_x.shape[0]):
        #     #classfied = y_data[i]
        #     output.append([vis_x[i], vis_y[i], y_train[i]])

        dist = []
        for i in range(vis_data.shape[0] - 1):
            d = np.linalg.norm(vis_data[vis_data.shape[0] - 1, :] - vis_data[i, :])
            dist.append(d)
        index = np.argmin(np.array(dist))

        return filenames[index]


class Point(object):
    x = 0
    y = 0

    def __init__(self, x, y):
        self.x = x
        self.y = y


class LineGenerator:
    def completeSketch(self, category, oriImg):
        targetImg = TSNE(category, oriImg)
        print('the closest neighbor is:')
        print(targetImg)
        #real images
        img1 = io.imread(oriImg, as_grey=True)
        img2 = io.imread(pathToImgLib + str(category) + '/' + targetImg, as_grey=True)

        descriptor_extractor = ORB(n_keypoints=num_keypoints)

        descriptor_extractor.detect_and_extract(img1)
        keypoints1 = descriptor_extractor.keypoints
        descriptors1 = descriptor_extractor.descriptors

        descriptor_extractor.detect_and_extract(img2)
        keypoints2 = descriptor_extractor.keypoints
        descriptors2 = descriptor_extractor.descriptors

        matches12 = match_descriptors(descriptors1, descriptors2, cross_check=True)

        lines = []
        prepoint = False
        for each in keypoints1:
            if each not in keypoints2:
                prepoint = True
                pt = Point(each[0], each[1])
                lines.append(pt)
            else:
                if prepoint:
                    pt = Point(0, 0)
                    lines.append(pt)
                prepoint = False

        return lines


s = zerorpc.Server(LineGenerator())
s.bind("tcp://0.0.0.0:4243")
s.run()

# testStart = LineGenerator()
# resultLines = testStart.completeSketch("airplane", "test_plane.png")
# for each in resultLines:
#     print each[0]
#     print each[1]